package com.honeypot.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CowrieEventDTO {

    @JsonProperty("eventid")
    private String eventId;

    @JsonProperty("session")
    private String session;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("src_ip")
    private String srcIp;

    @JsonProperty("src_port")
    private Integer srcPort;

    @JsonProperty("dst_ip")
    private String dstIp;

    @JsonProperty("dst_port")
    private Integer dstPort;

    @JsonProperty("protocol")
    private String protocol;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("input")
    private String input;

    @JsonProperty("message")
    private String message;

    @JsonProperty("url")
    private String url;

    @JsonProperty("shasum")
    private String sha256;

    @JsonProperty("outfile")
    private String outfile;

    @JsonProperty("duration")
    private Double duration;

    @JsonProperty("country")
    private String country;

    @JsonProperty("city")
    private String city;
}
