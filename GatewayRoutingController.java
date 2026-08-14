package com.honeypot.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class GatewayRoutingController {

    private final RestClient restClient;

    @Value("${services.attack-service.url:http://localhost:8081}")
    private String attackServiceUrl;

    @Value("${services.analytics-service.url:http://localhost:8083}")
    private String analyticsServiceUrl;

    @Value("${services.ml-service.url:http://localhost:8000}")
    private String mlServiceUrl;

    public GatewayRoutingController(RestClient restClient) {
        this.restClient = restClient;
    }

    @RequestMapping(value = "/api/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS})
    public ResponseEntity<?> routeRequest(
            HttpServletRequest request,
            @RequestBody(required = false) byte[] body
    ) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return ResponseEntity.ok().build();
        }

        String path = request.getRequestURI();
        String targetBaseUrl = resolveTargetService(path);

        if (targetBaseUrl == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "No route found for path: " + path);
            err.put("status", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        String queryString = request.getQueryString();
        String targetUrl = targetBaseUrl + path + (queryString != null ? "?" + queryString : "");

        try {
            HttpMethod method = HttpMethod.valueOf(request.getMethod());

            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames != null && headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                if (!name.equalsIgnoreCase("host") && !name.equalsIgnoreCase("content-length")) {
                    headers.addAll(name, Collections.list(request.getHeaders(name)));
                }
            }
            if (headers.getContentType() == null && body != null && body.length > 0) {
                headers.setContentType(MediaType.APPLICATION_JSON);
            }

            RestClient.RequestBodySpec requestSpec = restClient.method(method)
                    .uri(URI.create(targetUrl))
                    .headers(h -> h.addAll(headers));

            if (body != null && body.length > 0) {
                requestSpec.body(body);
            }

            ResponseEntity<byte[]> response = requestSpec.retrieve().toEntity(byte[].class);
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Service unavailable at: " + targetBaseUrl);
            err.put("path", path);
            err.put("message", e.getMessage());
            err.put("status", 503);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(err);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Gateway Routing Error");
            err.put("message", e.getMessage());
            err.put("status", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("gateway", "UP");
        status.put("port", 8080);
        status.put("routes", Map.of(
                "/api/attacks/**", attackServiceUrl,
                "/api/logs/**", attackServiceUrl,
                "/api/dashboard/**", analyticsServiceUrl,
                "/api/ai/**", mlServiceUrl
        ));
        return ResponseEntity.ok(status);
    }

    private String resolveTargetService(String path) {
        if (path.startsWith("/api/attacks") || path.startsWith("/api/logs")) {
            return attackServiceUrl;
        } else if (path.startsWith("/api/dashboard")) {
            return analyticsServiceUrl;
        } else if (path.startsWith("/api/ai")) {
            return mlServiceUrl;
        }
        return null;
    }
}
