package com.yuvaraj.blog.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.yuvaraj.blog.exceptions.signIn.SignInMaxSessionReachedException;
import com.yuvaraj.blog.helpers.ErrorCode;
import com.yuvaraj.blog.models.db.CustomerEntity;
import com.yuvaraj.blog.models.db.PasswordEntity;
import com.yuvaraj.blog.models.db.SignInEntity;
import com.yuvaraj.blog.models.inbuiltClass.CustomUser;
import com.yuvaraj.blog.models.signIn.SignInRequest;
import com.yuvaraj.blog.repositories.SignInRepository;
import com.yuvaraj.blog.services.CustomerService;
import com.yuvaraj.blog.services.PasswordService;
import com.yuvaraj.blog.services.SignInService;
import com.yuvaraj.security.helpers.DateHelper;
import com.yuvaraj.security.helpers.JsonHelper;
import com.yuvaraj.security.helpers.TokenType;
import com.yuvaraj.security.models.AuthSuccessfulResponse;
import com.yuvaraj.security.models.DefaultToken;
import com.yuvaraj.security.models.token.RefreshToken;
import com.yuvaraj.security.models.token.SessionToken;
import com.yuvaraj.security.providers.SimpleSymmetricCipherProvider;
import com.yuvaraj.security.services.JwtManagerService;
import com.yuvaraj.security.services.TokenValidationService;
import com.yuvaraj.security.services.cipher.symmetric.SimpleSymmetricCipher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.yuvaraj.blog.models.db.SignInEntity.Status.FORCED_SIGN_OUT;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class SignInServiceImpl implements SignInService, UserDetailsService {

    private final SignInRepository signInRepository;
    private final CustomerService customerService;
    private final PasswordService passwordService;
    private final TokenValidationService tokenValidationService;
    private final JwtManagerService jwtManagerService;

    @Override
    public UserDetails loadUserByUsername(String signInRequestString) throws UsernameNotFoundException {
        SignInRequest signInRequest = null;
        try {
            signInRequest = new ObjectMapper().readValue(signInRequestString, SignInRequest.class);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: Error while readValue signInRequestString={}, errorMessage={}", signInRequestString, e.getMessage());
            throw new UsernameNotFoundException("Unexpected error");
        }
        String email = signInRequest.getEmailAddress();
        log.info("[{}]: Load User By Username: Attempting to login", email);
        CustomerEntity customerEntity = customerService.findByEmail(email);
        if (null == customerEntity) {
            log.info("[{}]:Load User By Username: Customer Not Found", email);
            throw new UsernameNotFoundException("Customer not found");
        }
        if (customerEntity.getStatus().equals(CustomerEntity.Status.SUCCESS.getStatus())) {
            PasswordEntity passwordEntity = passwordService.getByCustomerEntity(customerEntity);
            Preconditions.checkNotNull(passwordEntity, "Password table not found = " + email);
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(customerEntity.getAuthorityEntity().getRole()));
            log.info("[{}]:Load User By Username: Success", email);
            //TODO: Inbuilt User got many implementations please try to take advantage of that
            return new CustomUser(signInRequest, customerEntity.getId(), customerEntity.getEmail(), passwordEntity.getPassword(), authorities);
        }
        //TODO: Handle properly
        log.info("[{}]:Load User By Username: Customer is not in {} status, userCurrentStatus={}", email, CustomerEntity.Status.SUCCESS.getStatus(), customerEntity.getStatus());
        throw new UsernameNotFoundException(" Customer is not in allowed status");
    }

    @Override
    public void validateRefreshToken(String authorization, String customerId) throws Exception {
        Preconditions.checkArgument(null != authorization && !authorization.isEmpty(), "Validate Refresh Token: authorization cannot be null or empty");
        Preconditions.checkArgument(null != customerId && !customerId.isEmpty(), "Validate Refresh Token: customerId cannot be null or empty");
        DefaultToken defaultToken = (DefaultToken) jwtManagerService.extractJwtPayload(authorization, DefaultToken.class);
        Preconditions.checkNotNull(defaultToken, "Validate Refresh Token: Unable to extract jwt payload");
        tokenValidationService.verifyToken(defaultToken, List.of(TokenType.REFRESH.getType()));
        SimpleSymmetricCipher simpleSymmetricCipher = (new SimpleSymmetricCipherProvider()).get();
        RefreshToken refreshToken = new ObjectMapper().convertValue(new ObjectMapper().readTree(simpleSymmetricCipher.decrypt(defaultToken.getSecret())), RefreshToken.class);
        Preconditions.checkNotNull(refreshToken, "Validate Refresh Token: Unable to extract default token secret");
        Preconditions.checkArgument(null != refreshToken.getCustomerId() && !refreshToken.getCustomerId().isEmpty(), "Validate Refresh Token: customerId cannot be null or empty in the token");
        Preconditions.checkArgument(refreshToken.getCustomerId().equals(customerId), "Validate Refresh Token: requested and token customerId in the token not tally");
        //TODO: Upsert refresh token tab
        CustomerEntity customerEntity = customerService.findById(refreshToken.getCustomerId());
        Preconditions.checkNotNull(customerEntity, "customer entity is null cannot find = " + refreshToken.getCustomerId());
        if (customerEntity.getStatus().equals(CustomerEntity.Status.SUCCESS.getStatus())) {
            checkIfRefreshTokenCreatedDateSyncWithOurSignInTab(customerEntity, defaultToken.getCreateTime());
            return;
        }
        //TODO: Handle proepr error
        throw new AccessDeniedException("Customer not in success state");

    }

    private void checkIfRefreshTokenCreatedDateSyncWithOurSignInTab(CustomerEntity customerEntity, long createTime) throws AccessDeniedException {
        Page<SignInEntity> signInEntityPage = findLatestSignInData(customerEntity, SignInEntity.Status.ACTIVE.getStatus(), PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdDate")));
        if (null == signInEntityPage || signInEntityPage.getContent().isEmpty()) {
            //TODO: Handle proepr error
            log.error("[{}]: Sign in tab is empty", customerEntity.getId());
            throw new AccessDeniedException("Sign in tab is empty");
        }
        for (SignInEntity signInEntity : signInEntityPage.getContent()) {
            if (createTime == signInEntity.getRefreshTokenGenerationTime() && signInEntity.getStatus().equals(SignInEntity.Status.ACTIVE.getStatus())) {
                log.info("[{}]: Yay! Found identical refresh token generation time with refresh token that provided.", customerEntity.getId());
                return;
            }
        }
        //TODO: Handle proepr error
        log.error("[{}]: Ohho! Refresh token generation time is not tally with out sign in tab. So we proceeding to throw exception", customerEntity.getId());
        throw new AccessDeniedException("Ohho! Refresh token generation time is not tally with out sign in tab");
    }

    @Override
    public void validateSessionToken(String authorization, String customerId) throws Exception {
        Preconditions.checkArgument(null != authorization && !authorization.isEmpty(), "Validate Refresh Token: authorization cannot be null or empty");
        Preconditions.checkArgument(null != customerId && !customerId.isEmpty(), "Validate Refresh Token: customerId cannot be null or empty");
        DefaultToken defaultToken = (DefaultToken) jwtManagerService.extractJwtPayload(authorization, DefaultToken.class);
        Preconditions.checkNotNull(defaultToken, "Validate Refresh Token: Unable to extract jwt payload");
        tokenValidationService.verifyToken(defaultToken, List.of(TokenType.SESSION.getType()));
        SimpleSymmetricCipher simpleSymmetricCipher = (new SimpleSymmetricCipherProvider()).get();
        SessionToken sessionToken = new ObjectMapper().convertValue(new ObjectMapper().readTree(simpleSymmetricCipher.decrypt(defaultToken.getSecret())), SessionToken.class);
        Preconditions.checkNotNull(sessionToken, "Validate Refresh Token: Unable to extract default token secret");
        Preconditions.checkArgument(null != sessionToken.getCustomerId() && !sessionToken.getCustomerId().isEmpty(), "Validate Refresh Token: customerId cannot be null or empty in the token");
        Preconditions.checkArgument(sessionToken.getCustomerId().equals(customerId), "Validate Refresh Token: requested and token customerId in the token not tally");
        //TODO: Return something
    }

    @Override
    public void handleSignInData(CustomUser user, AuthSuccessfulResponse authSuccessfulResponse, SignInRequest signInRequest) throws SignInMaxSessionReachedException {
        log.info("[{}]: Handling Sign In Data request={}", user.getCustomerId(), JsonHelper.toJson(user));
        CustomerEntity customerEntity = customerService.findById(user.getCustomerId());
        if (null == customerEntity) {
            log.info("[{}]:Handling Sign In Data: Customer Not Found", user.getCustomerId());
            throw new UsernameNotFoundException("Customer not found");
        }
        if (customerEntity.getStatus().equals(CustomerEntity.Status.SUCCESS.getStatus())) {
            validateIfSignInSessionMaxReached(customerEntity, signInRequest);
            insertSignInRecord(customerEntity, authSuccessfulResponse.getGenerationTimestamp(), signInRequest);
            return;
        }
        //TODO: Handle properly
        log.info("[{}]:Load User By Username: Customer is not in {} status, userCurrentStatus={}", user.getCustomerId(), CustomerEntity.Status.SUCCESS.getStatus(), customerEntity.getStatus());
        throw new UsernameNotFoundException(" Customer is not in allowed status");
    }

    private void insertSignInRecord(CustomerEntity customerEntity, long generationTimeStamp, SignInRequest signInRequest) {
        SignInEntity signInEntity = new SignInEntity();
        signInEntity.setCustomerEntity(customerEntity);
        signInEntity.setRefreshTokenGenerationTime(generationTimeStamp);
        signInEntity.setDeviceName(signInRequest.getDeviceName());
        signInEntity.setDeviceType(signInRequest.getDeviceType());
        signInEntity.setDeviceSubType(signInRequest.getDeviceSubtype());
        signInEntity.setIpAddress(signInRequest.getIpAddress());
        signInEntity.setSignInDate(DateHelper.convertTimeStampToDate(generationTimeStamp));
        signInEntity.setStatus(SignInEntity.Status.ACTIVE.getStatus());
        save(signInEntity);
    }

    private void validateIfSignInSessionMaxReached(CustomerEntity customerEntity, SignInRequest signInRequest) throws SignInMaxSessionReachedException {
        log.info("[{}]: Validate if sign in session max reached", customerEntity.getId());
        Page<SignInEntity> signInEntityPage = findLatestSignInData(customerEntity, SignInEntity.Status.ACTIVE.getStatus(), PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdDate")));
        if (null == signInEntityPage || signInEntityPage.getContent().isEmpty()) {
            log.info("[{}]: User sign in is empty or not yet reach max so it's good to go", customerEntity.getId());
            return;
        }
        for (SignInEntity signInEntity : signInEntityPage.getContent()) {
            if (signInEntity.getIpAddress().equals(signInRequest.getIpAddress())) {
                signInEntity.setStatus(FORCED_SIGN_OUT.getStatus());
                update(signInEntity);
                log.info("[{}]: Existing ip address found in sign in so we are {} it.", signInEntity.getStatus(), customerEntity.getId());
                return;
            }
        }
        if (signInEntityPage.getContent().size() != SignInEntity.MAX_SIGN_SESSION) {
            log.info("[{}]: User sign in is empty or not yet reach max so it's good to go", customerEntity.getId());
            return;
        }
        //TODO: error handling
        log.info("[{}]: User sign reached the max number of session={}", customerEntity.getId(), SignInEntity.MAX_SIGN_SESSION);
        throw new SignInMaxSessionReachedException("Max Number of session reached", new ArrayList<>(), ErrorCode.MAX_NUMBER_OF_SESSION_REACHED);
    }

    private SignInEntity save(SignInEntity signInEntity) {
        return signInRepository.save(signInEntity);
    }

    private SignInEntity update(SignInEntity signInEntity) {
        return signInRepository.save(signInEntity);
    }

    private Page<SignInEntity> findLatestSignInData(CustomerEntity customerEntity, String status, PageRequest createdDate) {
        return signInRepository.findLatestSignInData(customerEntity, status, createdDate);
    }
}
