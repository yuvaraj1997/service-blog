package com.yuvaraj.blog.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuvaraj.blog.exceptions.signup.CustomerAlreadyExistException;
import com.yuvaraj.blog.models.controllers.v1.signup.postSignUp.PostSignUpRequest;
import com.yuvaraj.blog.models.controllers.v1.signup.postSignUp.PostSignUpResponse;
import com.yuvaraj.blog.services.SignUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.yuvaraj.blog.helpers.ResponseHelper.ok;

@RestController
@RequestMapping(path = "v1/signup")
@Slf4j
public class SignUpController {

    @Autowired
    SignUpService signUpService;

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postSignUp(@Valid @RequestBody PostSignUpRequest postSignUpRequest, HttpServletRequest httpServletRequest) throws CustomerAlreadyExistException {
        //todo: remove logging password
        String logMessage = String.format("%s %s", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        log.info("Initiate to process {}, request={}", logMessage, new ObjectMapper().valueToTree(postSignUpRequest));
        PostSignUpResponse postSignUpResponse = signUpService.processPostSignUp(postSignUpRequest);
        log.info("Successfully processed {}, request={}, response={}", logMessage, new ObjectMapper().valueToTree(postSignUpRequest)
                , new ObjectMapper().valueToTree(postSignUpResponse));
        return ok(postSignUpResponse);
    }
}
