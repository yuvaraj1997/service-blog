package com.yuvaraj.blog.models.signIn;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SignInRequest {

    @JsonProperty("emailAddress")
    private String emailAddress;

    @JsonProperty("password")
    private String password;

    @JsonProperty("ipAddress")
    private String ipAddress;

    @JsonProperty("deviceName")
    private String deviceName;

    @JsonProperty("deviceType")
    private String deviceType;

    @JsonProperty("deviceSubtype")
    private String deviceSubtype;
}
