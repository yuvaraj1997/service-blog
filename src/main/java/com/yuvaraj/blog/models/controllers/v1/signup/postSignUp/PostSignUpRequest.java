package com.yuvaraj.blog.models.controllers.v1.signup.postSignUp;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PostSignUpRequest {

    @JsonProperty("fullName")
    @NotBlank(message = "Full name is mandatory")
    @Pattern(
            regexp = "^[a-zA-Z/.@'\\- ]*$",
            message = "Full name should be valid"
    )
    private String fullName;

    @JsonProperty("emailAddress")
    @NotBlank(message = "Email address is mandatory")
    @Email(message = "Email address should be valid")
    private String emailAddress;

    @JsonProperty("password")
    @NotBlank(message = "Password is mandatory")
    //TODO: Password strength
    private String password;
}
