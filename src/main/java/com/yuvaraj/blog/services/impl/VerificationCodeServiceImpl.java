package com.yuvaraj.blog.services.impl;

import com.google.common.base.Preconditions;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeExpiredException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeMaxLimitReachedException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeResendNotAllowedException;
import com.yuvaraj.blog.helpers.ErrorCode;
import com.yuvaraj.blog.models.db.VerificationCodeEntity;
import com.yuvaraj.blog.repositories.VerificationCodeRepository;
import com.yuvaraj.blog.services.VerificationCodeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.yuvaraj.blog.helpers.DateHelper.nowDateAddMinutes;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    @Override
    public void sendSignUpActivation(String identifier) throws VerificationCodeMaxLimitReachedException, VerificationCodeResendNotAllowedException {
        Preconditions.checkNotNull(identifier, "identifier cannot be null");
        int resendRetriesCount = 1;
        log.info("[{}]: Initiating send sign up activation. identifier={}", identifier, identifier);
        VerificationCodeEntity verificationCodeEntity;
        Page<VerificationCodeEntity> verificationCodeEntityPage = verificationCodeRepository.findLatestByIdentifierAndType(identifier, VerificationCodeEntity.Type.SIGN_UP_ACTIVATION.getType(), PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdDate")));
        if (null != verificationCodeEntityPage && !verificationCodeEntityPage.getContent().isEmpty()) {
            verificationCodeEntity = verificationCodeEntityPage.getContent().get(0);
            checkIfItEligibleToResend(verificationCodeEntity);
            log.info("[{}]: We have a existing record. identifier={}", identifier, identifier);
            if (verificationCodeEntity.isExpired() && !verificationCodeEntity.getStatus().equals(VerificationCodeEntity.Status.EXPIRED.getStatus())) {
                log.info("[{}]: Record has been expired we will setting the status as expired. identifier={}", identifier, identifier);
                verificationCodeEntity.setStatus(VerificationCodeEntity.Status.EXPIRED.getStatus());
            } else {
                log.info("[{}]: We will be sending new verification code current record will mark it as user requested again. identifier={}", identifier, identifier);
                verificationCodeEntity.setStatus(VerificationCodeEntity.Status.USER_REQUESTED_AGAIN.getStatus());
            }
            verificationCodeEntity = update(verificationCodeEntity);
            checkIfMaxReached(verificationCodeEntity);
            resendRetriesCount = verificationCodeEntity.getNextResendRetriesCount();
        }
        verificationCodeEntity = new VerificationCodeEntity();
        verificationCodeEntity.setIdentifier(identifier);
        verificationCodeEntity.setType(VerificationCodeEntity.Type.SIGN_UP_ACTIVATION.getType());
        verificationCodeEntity.setExpiryDate(nowDateAddMinutes(5));
        verificationCodeEntity.setResendRetries(resendRetriesCount);
        verificationCodeEntity.setStatus(VerificationCodeEntity.Status.PENDING.getStatus());
        verificationCodeEntity = save(verificationCodeEntity);
        log.info("[{}]: Successfully send sign up activation. identifier={}, verificationCodeId={}", identifier, identifier, verificationCodeEntity.getId());
    }

    private VerificationCodeEntity save(VerificationCodeEntity verificationCodeEntity) {
        return verificationCodeRepository.save(verificationCodeEntity);
    }

    private VerificationCodeEntity update(VerificationCodeEntity verificationCodeEntity) {
        return verificationCodeRepository.save(verificationCodeEntity);
    }

    private void checkIfMaxReached(VerificationCodeEntity verificationCodeEntity) throws VerificationCodeMaxLimitReachedException {
        if (verificationCodeEntity.isMaxReached()) {
            if (verificationCodeEntity.isRequestUnlocked()) {
                return;
            }
            log.info("[{}]: Verification max limit request reached. identifier={}", verificationCodeEntity.getIdentifier(), verificationCodeEntity.getIdentifier());
            throw new VerificationCodeMaxLimitReachedException("Verification max request limit reached.", ErrorCode.VERIFICATION_CODE_REQUEST_LIMIT_REACH);
        }
    }

    private void checkIfItEligibleToResend(VerificationCodeEntity verificationCodeEntity) throws VerificationCodeResendNotAllowedException {
        if (!verificationCodeEntity.isItEligibleToResendVerification()) {
            log.info("[{}]: It is not eligible to resend. Too Soon. identifier={}", verificationCodeEntity.getIdentifier(), verificationCodeEntity.getIdentifier());
            throw new VerificationCodeResendNotAllowedException("Verification max request limit reached.", ErrorCode.VERIFICATION_CODE_RESEND_NOT_ALLOWED);
        }
    }

    private void checkIfVerificationIsExpired(VerificationCodeEntity verificationCodeEntity) throws VerificationCodeExpiredException {
        if (verificationCodeEntity.isExpired()) {
            log.info("[{}]: Verification already expired will ask user to request again. identifier={}", verificationCodeEntity.getIdentifier(), verificationCodeEntity.getIdentifier());
            throw new VerificationCodeExpiredException("Verification already expired will ask user to request again", ErrorCode.VERIFICATION_CODE_ALREADY_EXPIRED);
        }
    }
}
