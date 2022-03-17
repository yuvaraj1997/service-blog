package com.yuvaraj.blog.services;

import com.yuvaraj.blog.exceptions.signup.CustomerAlreadyExistException;
import com.yuvaraj.blog.models.controllers.v1.signup.PostSignUpRequest;
import com.yuvaraj.blog.models.controllers.v1.signup.PostSignUpResponse;

/**
 *
 */
public interface SignUpService {

    /**
     * @param postSignUpRequest Object request
     * @return PostSignUpResponse
     */
    PostSignUpResponse processPostSignUp(PostSignUpRequest postSignUpRequest) throws CustomerAlreadyExistException;
}
