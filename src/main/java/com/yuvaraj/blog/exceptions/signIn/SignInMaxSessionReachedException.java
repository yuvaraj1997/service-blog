package com.yuvaraj.blog.exceptions.signIn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yuvaraj.blog.helpers.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SignInMaxSessionReachedException extends Exception {

    private final String errorMessage;
    private final List<DeviceDetails> deviceDetails;
    private final ErrorCode errorCode;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    @NoArgsConstructor
    public class DeviceDetails {

        @JsonProperty("id")
        private String id;

        @JsonProperty("type")
        private String type;

        @JsonProperty("subType")
        private String subType;

        @JsonProperty("ipAddress")
        private String ipAddress;

        @JsonProperty("status")
        private String status;

        @JsonProperty("signIndDate")
        private String signIndDate;
    }
}
