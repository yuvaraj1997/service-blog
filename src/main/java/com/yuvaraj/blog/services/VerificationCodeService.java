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
     * @param type       VerificationCodeEntity.Type request
     */
    void sendVerification(String identifier, VerificationCodeEntity.Type type) throws VerificationCodeMaxLimitReachedException, VerificationCodeResendNotAllowedException;

    /**
     * @param id String request
     */
    VerificationCodeEntity findById(String id);

    /**
     * @param id         String request
     * @param identifier String request
     * @param type       VerificationCodeEntity.Type request
     */
    void isVerificationIdIsValidToProceedVerification(String id, String identifier, VerificationCodeEntity.Type type) throws InvalidArgumentException, VerificationCodeExpiredException;

    /**
     * @param id String request
     */
    void markAsVerified(String id) throws InvalidArgumentException;
}
