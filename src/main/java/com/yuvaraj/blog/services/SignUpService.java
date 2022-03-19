package com.yuvaraj.blog.services;

import com.yuvaraj.blog.exceptions.CustomerNotFoundException;
import com.yuvaraj.blog.exceptions.signup.CustomerAlreadyExistException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeMaxLimitReachedException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeResendNotAllowedException;
import com.yuvaraj.blog.models.controllers.v1.signup.postResendVerification.PostResendVerificationRequest;
import com.yuvaraj.blog.models.controllers.v1.signup.postSignUp.PostSignUpRequest;
import com.yuvaraj.blog.models.controllers.v1.signup.postSignUp.PostSignUpResponse;

/**
 *
 */
public interface SignUpService {

    /**
     * @param postSignUpRequest Object request
     * @return PostSignUpResponse
     */
    PostSignUpResponse processPostSignUp(PostSignUpRequest postSignUpRequest) throws CustomerAlreadyExistException, VerificationCodeMaxLimitReachedException, VerificationCodeResendNotAllowedException;


    /**
     * @param postResendVerificationRequest String request
     */
    void processPostResendVerification(PostResendVerificationRequest postResendVerificationRequest) throws CustomerAlreadyExistException, CustomerNotFoundException, VerificationCodeMaxLimitReachedException, VerificationCodeResendNotAllowedException;
}
