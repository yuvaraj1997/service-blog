package com.yuvaraj.blog.services;

import com.yuvaraj.blog.exceptions.verification.VerificationCodeMaxLimitReachedException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeResendNotAllowedException;

/**
 *
 */
public interface VerificationCodeService {

    /**
     * @param identifier String request
     */
    void sendSignUpActivation(String identifier) throws VerificationCodeMaxLimitReachedException, VerificationCodeResendNotAllowedException;
}
