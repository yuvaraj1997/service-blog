package com.yuvaraj.blog.models.controllers.v1.forgotPassword.postForgotPasswordUpsert;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PostForgotPasswordUpsertRequest {

    @JsonProperty("token")
    @NotBlank(message = "token is mandatory")
    private String token;

    @JsonProperty("customerId")
    @NotBlank(message = "customerId is mandatory")
    private String customerId;

    @JsonProperty("password")
    @NotBlank(message = "password is mandatory")
    //TODO: Password strength
    private String password;
}
