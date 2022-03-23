package com.yuvaraj.blog.services.impl;

import com.yuvaraj.blog.exceptions.InvalidArgumentException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeExpiredException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeMaxLimitReachedException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeResendNotAllowedException;
import com.yuvaraj.blog.helpers.ErrorCode;
import com.yuvaraj.blog.models.controllers.v1.forgotPassword.postForgotPassword.PostForgotPasswordRequest;
import com.yuvaraj.blog.models.controllers.v1.forgotPassword.postForgotPasswordUpsert.PostForgotPasswordUpsertRequest;
import com.yuvaraj.blog.models.db.CustomerEntity;
import com.yuvaraj.blog.models.db.VerificationCodeEntity;
import com.yuvaraj.blog.services.CustomerService;
import com.yuvaraj.blog.services.ForgotPasswordService;
import com.yuvaraj.blog.services.PasswordService;
import com.yuvaraj.blog.services.VerificationCodeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final CustomerService customerService;
    private final VerificationCodeService verificationCodeService;
    private final PasswordService passwordService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void processPostForgotPassword(PostForgotPasswordRequest postForgotPasswordRequest) throws InvalidAlgorithmParameterException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        CustomerEntity customerEntity = customerService.findByEmailTypeSubtypeAndStatuses(
                postForgotPasswordRequest.getEmailAddress(),
                CustomerEntity.Type.CUSTOMER.getType(),
                CustomerEntity.SubType.NA.getSubType(),
                List.of(CustomerEntity.Status.SUCCESS.getStatus())
        );
        if (null == customerEntity) {
            log.info("[{}]: Forgot Password: Customer not found or not in success state emailAddress={}, we will still return 200 to prevent any email leakage", postForgotPasswordRequest.getEmailAddress(), postForgotPasswordRequest.getEmailAddress());
            return;
        }
        try {
            verificationCodeService.sendVerification(customerEntity.getId(), VerificationCodeEntity.Type.FORGOT_PASSWORD);
        } catch (VerificationCodeMaxLimitReachedException e) {
            log.info("[{}]: VerificationCodeMaxLimitReachedException: Forgot Password: Customer verification code max limit request reached, we will still return 200 to prevent any email leakage. emailAddress={}, customerId={}, infoMessage={}", postForgotPasswordRequest.getEmailAddress(), customerEntity.getEmail(), customerEntity.getId(), e.getErrorMessage());
        } catch (VerificationCodeResendNotAllowedException e) {
            log.info("[{}]: VerificationCodeResendNotAllowedException: Forgot Password: Customer verification resend not allowed, we will still return 200 to prevent any email leakage. emailAddress={}, customerId={}, infoMessage={}", postForgotPasswordRequest.getEmailAddress(), customerEntity.getEmail(), customerEntity.getId(), e.getErrorMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void processPostForgotPasswordUpsert(PostForgotPasswordUpsertRequest postForgotPasswordUpsertRequest) throws InvalidArgumentException, VerificationCodeExpiredException {
        //TODO: Token decryption
        verificationCodeService.isVerificationIdIsValidToProceedVerification(postForgotPasswordUpsertRequest.getToken(), postForgotPasswordUpsertRequest.getCustomerId(), VerificationCodeEntity.Type.SIGN_UP_ACTIVATION);
        CustomerEntity customerEntity = customerService.findById(postForgotPasswordUpsertRequest.getCustomerId());
        if (null == customerEntity) {
            log.error("[{}]: Process Post Forgot Password Upsert: Customer Not Found.", postForgotPasswordUpsertRequest.getCustomerId());
            throw new InvalidArgumentException("Customer Not Found", ErrorCode.INVALID_ARGUMENT);
        }
        if (!customerEntity.getStatus().equals(CustomerEntity.Status.SUCCESS.getStatus())) {
            log.error("[{}]:  Process Post Forgot Password Upsert: Customer status is not satisfy. customerId={}, status={}", customerEntity.getId(), customerEntity.getId(), customerEntity.getStatus());
            throw new InvalidArgumentException("Customer status is not satisfy.", ErrorCode.INVALID_ARGUMENT);
        }
        passwordService.upsertPassword(passwordEncoder.encode(postForgotPasswordUpsertRequest.getPassword()), postForgotPasswordUpsertRequest.getCustomerId());
        verificationCodeService.markAsVerified(postForgotPasswordUpsertRequest.getToken(), postForgotPasswordUpsertRequest.getCustomerId());
    }
}
