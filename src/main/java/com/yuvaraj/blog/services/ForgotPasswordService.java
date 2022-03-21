package com.yuvaraj.blog.services;

import com.yuvaraj.blog.exceptions.InvalidArgumentException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeExpiredException;
import com.yuvaraj.blog.models.controllers.v1.forgotPassword.postForgotPassword.PostForgotPasswordRequest;
import com.yuvaraj.blog.models.controllers.v1.forgotPassword.postForgotPasswordUpsert.PostForgotPasswordUpsertRequest;

/**
 *
 */
public interface ForgotPasswordService {

    /**
     * @param postForgotPasswordRequest Object request
     */
    void processPostForgotPassword(PostForgotPasswordRequest postForgotPasswordRequest);

    /**
     * @param postForgotPasswordUpsertRequest Object request
     */

    void processPostForgotPasswordUpsert(PostForgotPasswordUpsertRequest postForgotPasswordUpsertRequest) throws InvalidArgumentException, VerificationCodeExpiredException;
}
