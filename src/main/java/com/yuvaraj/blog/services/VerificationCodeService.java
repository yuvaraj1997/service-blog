package com.yuvaraj.blog.services;

import com.yuvaraj.blog.exceptions.InvalidArgumentException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeExpiredException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeMaxLimitReachedException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeResendNotAllowedException;
import com.yuvaraj.blog.models.db.VerificationCodeEntity;

/**
 *
 */
public interface VerificationCodeService {

    /**
     * @param identifier String request
     */
    void sendSignUpActivation(String identifier) throws VerificationCodeMaxLimitReachedException, VerificationCodeResendNotAllowedException;

    /**
     * @param id String request
     */
    VerificationCodeEntity findById(String id);

    /**
     * @param id String request
     */
    void isVerificationIdIsValidToProceedVerification(String id) throws InvalidArgumentException, VerificationCodeExpiredException;

    /**
     * @param id String request
     */
    void markAsVerified(String id) throws InvalidArgumentException;
}
