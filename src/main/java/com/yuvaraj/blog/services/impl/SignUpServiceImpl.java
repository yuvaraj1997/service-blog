package com.yuvaraj.blog.services.impl;

import com.yuvaraj.blog.exceptions.CustomerNotFoundException;
import com.yuvaraj.blog.exceptions.InvalidArgumentException;
import com.yuvaraj.blog.exceptions.signup.CustomerAlreadyExistException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeExpiredException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeMaxLimitReachedException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeResendNotAllowedException;
import com.yuvaraj.blog.helpers.ErrorCode;
import com.yuvaraj.blog.models.controllers.v1.signup.postResendVerification.PostResendVerificationRequest;
import com.yuvaraj.blog.models.controllers.v1.signup.postSignUp.PostSignUpRequest;
import com.yuvaraj.blog.models.controllers.v1.signup.postSignUp.PostSignUpResponse;
import com.yuvaraj.blog.models.controllers.v1.signup.postVerify.PostVerifyRequest;
import com.yuvaraj.blog.models.db.AuthorityEntity;
import com.yuvaraj.blog.models.db.CustomerEntity;
import com.yuvaraj.blog.models.db.VerificationCodeEntity;
import com.yuvaraj.blog.services.*;
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

import static com.yuvaraj.blog.helpers.DateHelper.convertDateForEndResult;
import static com.yuvaraj.blog.helpers.DateHelper.nowDate;

@Service
@Slf4j
@AllArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final CustomerService customerService;
    private final PasswordService passwordService;
    private final AuthorityService authorityService;
    private final VerificationCodeService verificationCodeService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PostSignUpResponse processPostSignUp(PostSignUpRequest postSignUpRequest) throws CustomerAlreadyExistException, VerificationCodeMaxLimitReachedException, VerificationCodeResendNotAllowedException, InvalidAlgorithmParameterException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        checkIfUserAlreadyExist(postSignUpRequest.getEmailAddress());
        CustomerEntity customerEntity = getAnyExistingRecordIfAvailable(postSignUpRequest.getEmailAddress());
        if (null != customerEntity) {
            return buildPostSignUpResponse(customerEntity, true);
        }
        customerEntity = createCustomerRecord(postSignUpRequest);
        passwordService.upsertPassword(passwordEncoder.encode(postSignUpRequest.getPassword()), customerEntity.getId());
        verificationCodeService.sendVerification(customerEntity.getId(), VerificationCodeEntity.Type.SIGN_UP_ACTIVATION);
        return buildPostSignUpResponse(customerEntity, false);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void processPostResendVerification(PostResendVerificationRequest postResendVerificationRequest) throws CustomerAlreadyExistException, CustomerNotFoundException, VerificationCodeMaxLimitReachedException, VerificationCodeResendNotAllowedException, InvalidAlgorithmParameterException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        checkIfUserAlreadyExist(postResendVerificationRequest.getEmailAddress());
        CustomerEntity customerEntity = getAnyExistingRecordIfAvailable(postResendVerificationRequest.getEmailAddress());
        if (null == customerEntity) {
            log.info("Customer not found to do resend verification emailAddress={}", postResendVerificationRequest.getEmailAddress());
            throw new CustomerNotFoundException("customer not found to resend verification", ErrorCode.CUSTOMER_NOT_FOUND);
        }
        verificationCodeService.sendVerification(customerEntity.getId(), VerificationCodeEntity.Type.SIGN_UP_ACTIVATION);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void processPostVerify(PostVerifyRequest postVerifyRequest) throws InvalidArgumentException, VerificationCodeExpiredException {
        verificationCodeService.isVerificationIdIsValidToProceedVerification(postVerifyRequest.getToken(), postVerifyRequest.getCustomerId(), VerificationCodeEntity.Type.SIGN_UP_ACTIVATION);
        CustomerEntity customerEntity = customerService.findById(postVerifyRequest.getCustomerId());
        if (null == customerEntity) {
            log.error("[{}]: Customer Not Found.", postVerifyRequest.getToken());
            throw new InvalidArgumentException("Customer Not Found", ErrorCode.INVALID_ARGUMENT);
        }
        if (!customerEntity.getStatus().equals(CustomerEntity.Status.VERIFICATION_PENDING.getStatus())) {
            log.error("[{}]: Customer status is not satisfy to verify. customerId={}, status={}", postVerifyRequest.getToken(), customerEntity.getId(), customerEntity.getStatus());
            throw new InvalidArgumentException("Customer status is not satisfy to verify", ErrorCode.INVALID_ARGUMENT);
        }
        verificationCodeService.markAsVerified(postVerifyRequest.getToken(), postVerifyRequest.getCustomerId());
        customerEntity.setStatus(CustomerEntity.Status.SUCCESS.getStatus());
        customerEntity.setCustomerCreatedDate(nowDate());
        customerService.update(customerEntity);
    }

    private CustomerEntity createCustomerRecord(PostSignUpRequest postSignUpRequest) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setType(CustomerEntity.Type.CUSTOMER.getType());
        customerEntity.setSubtype(CustomerEntity.SubType.NA.getSubType());
        customerEntity.setEmail(postSignUpRequest.getEmailAddress());
        customerEntity.setFullName(postSignUpRequest.getFullName());
        customerEntity.setAuthorityEntity(authorityService.getById(AuthorityEntity.Role.ROLE_CUSTOMER.getId()));
        customerEntity.setStatus(CustomerEntity.Status.VERIFICATION_PENDING.getStatus());
        return customerService.save(customerEntity);
    }

    private PostSignUpResponse buildPostSignUpResponse(CustomerEntity customerEntity, boolean verificationNeeded) {
        PostSignUpResponse postSignUpResponse = new PostSignUpResponse();
        postSignUpResponse.setCustomerId(customerEntity.getId());
        if (verificationNeeded) {
            postSignUpResponse.setStatus(CustomerEntity.Status.VERIFICATION_PENDING.getStatus());
        }
        postSignUpResponse.setDateCreated(convertDateForEndResult(customerEntity.getCreatedDate()));
        postSignUpResponse.setDateUpdated(convertDateForEndResult(customerEntity.getUpdatedDate()));
        return postSignUpResponse;
    }

    private CustomerEntity getAnyExistingRecordIfAvailable(String emailAddress) {
        return customerService.findByEmailTypeSubtypeAndStatuses(
                emailAddress,
                CustomerEntity.Type.CUSTOMER.getType(),
                CustomerEntity.SubType.NA.getSubType(),
                List.of(
                        CustomerEntity.Status.VERIFICATION_PENDING.getStatus()
                )
        );
    }

    private void checkIfUserAlreadyExist(String emailAddress) throws CustomerAlreadyExistException {
        CustomerEntity customerEntity = customerService.findByEmailTypeSubtypeAndStatuses(
                emailAddress,
                CustomerEntity.Type.CUSTOMER.getType(),
                CustomerEntity.SubType.NA.getSubType(),
                List.of(CustomerEntity.Status.SUCCESS.getStatus())
        );
        if (null != customerEntity) {
            log.info("User already exist emailAddress={}, type={}, subType={}, status={}",
                    emailAddress,
                    CustomerEntity.Type.CUSTOMER.getType(),
                    CustomerEntity.SubType.NA.getSubType(),
                    List.of(CustomerEntity.Status.SUCCESS.getStatus())
            );
            throw new CustomerAlreadyExistException("User already exit", ErrorCode.CUSTOMER_ALREADY_EXIST);
        }
    }
}
