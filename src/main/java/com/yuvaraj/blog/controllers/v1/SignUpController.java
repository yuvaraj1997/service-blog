package com.yuvaraj.blog.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuvaraj.blog.exceptions.CustomerNotFoundException;
import com.yuvaraj.blog.exceptions.InvalidArgumentException;
import com.yuvaraj.blog.exceptions.signup.CustomerAlreadyExistException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeExpiredException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeMaxLimitReachedException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeResendNotAllowedException;
import com.yuvaraj.blog.models.controllers.v1.signup.postResendVerification.PostResendVerificationRequest;
import com.yuvaraj.blog.models.controllers.v1.signup.postSignUp.PostSignUpRequest;
import com.yuvaraj.blog.models.controllers.v1.signup.postSignUp.PostSignUpResponse;
import com.yuvaraj.blog.models.controllers.v1.signup.postVerify.PostVerifyRequest;
import com.yuvaraj.blog.services.SignUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.yuvaraj.blog.helpers.ResponseHelper.ok;
import static com.yuvaraj.blog.helpers.ResponseHelper.okAsJson;

@RestController
@RequestMapping(path = "v1/signup")
@Slf4j
public class SignUpController {

    @Autowired
    SignUpService signUpService;

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postSignUp(@Valid @RequestBody PostSignUpRequest postSignUpRequest, HttpServletRequest httpServletRequest) throws CustomerAlreadyExistException, VerificationCodeMaxLimitReachedException, VerificationCodeResendNotAllowedException, InvalidAlgorithmParameterException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //todo: remove logging password
        String logMessage = String.format("%s %s", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        log.info("Initiate to process {}, request={}", logMessage, new ObjectMapper().valueToTree(postSignUpRequest));
        PostSignUpResponse postSignUpResponse = signUpService.processPostSignUp(postSignUpRequest);
        log.info("Successfully processed {}, request={}, response={}", logMessage, new ObjectMapper().valueToTree(postSignUpRequest)
                , new ObjectMapper().valueToTree(postSignUpResponse));
        return ok(postSignUpResponse);
    }


    @PostMapping(path = "resend/verification", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postResendVerification(@Valid @RequestBody PostResendVerificationRequest postResendVerificationRequest, HttpServletRequest httpServletRequest) throws CustomerAlreadyExistException, VerificationCodeMaxLimitReachedException, CustomerNotFoundException, VerificationCodeResendNotAllowedException, InvalidAlgorithmParameterException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String logMessage = String.format("%s %s", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        log.info("Initiate to process {}, request={}", logMessage, new ObjectMapper().valueToTree(postResendVerificationRequest));
        signUpService.processPostResendVerification(postResendVerificationRequest);
        log.info("Successfully processed {}, request={}", logMessage, new ObjectMapper().valueToTree(postResendVerificationRequest));
        return okAsJson();
    }

    @PostMapping(path = "verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postVerify(@Valid @RequestBody PostVerifyRequest postVerifyRequest, HttpServletRequest httpServletRequest) throws CustomerAlreadyExistException, VerificationCodeMaxLimitReachedException, CustomerNotFoundException, VerificationCodeResendNotAllowedException, InvalidArgumentException, VerificationCodeExpiredException {
        String logMessage = String.format("%s %s", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        log.info("Initiate to process {}, request={}", logMessage, new ObjectMapper().valueToTree(postVerifyRequest));
        signUpService.processPostVerify(postVerifyRequest);
        log.info("Successfully processed {}, request={}", logMessage, new ObjectMapper().valueToTree(postVerifyRequest));
        return okAsJson();
    }
}
