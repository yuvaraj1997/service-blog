package com.yuvaraj.blog.models.controllers.v1.signup;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PostSignUpRequest {

    @JsonProperty("emailAddress")
    @NotBlank(message = "Email address is mandatory")
    @Email(message = "Email address should be valid")
    private String emailAddress;
}
