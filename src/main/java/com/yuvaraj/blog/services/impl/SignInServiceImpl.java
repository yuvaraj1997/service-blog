package com.yuvaraj.blog.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.yuvaraj.blog.models.db.CustomerEntity;
import com.yuvaraj.blog.models.db.PasswordEntity;
import com.yuvaraj.blog.models.inbuiltClass.CustomUser;
import com.yuvaraj.blog.services.CustomerService;
import com.yuvaraj.blog.services.PasswordService;
import com.yuvaraj.blog.services.SignInService;
import com.yuvaraj.security.helpers.TokenType;
import com.yuvaraj.security.models.DefaultToken;
import com.yuvaraj.security.models.token.RefreshToken;
import com.yuvaraj.security.providers.SimpleSymmetricCipherProvider;
import com.yuvaraj.security.services.JwtManagerService;
import com.yuvaraj.security.services.TokenValidationService;
import com.yuvaraj.security.services.cipher.symmetric.SimpleSymmetricCipher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class SignInServiceImpl implements SignInService, UserDetailsService {

    private final CustomerService customerService;
    private final PasswordService passwordService;
    private final TokenValidationService tokenValidationService;
    private final JwtManagerService jwtManagerService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
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
            return new CustomUser(customerEntity.getId(), customerEntity.getEmail(), passwordEntity.getPassword(), authorities);
        }
        //TODO: Handle properly
        log.info("[{}]:Load User By Username: Customer is not in {} status, userCurrentStatus={}", email, CustomerEntity.Status.SUCCESS.getStatus(), customerEntity.getStatus());
        throw new UsernameNotFoundException(" Customer is not in allowed status");
    }

    @Override
    public void validateRefreshToken(String token, String customerId) throws Exception {
        Preconditions.checkArgument(null != token && token.isEmpty(), "Validate Refresh Token: token cannot be null or empty");
        Preconditions.checkArgument(null != customerId && customerId.isEmpty(), "Validate Refresh Token: customerId cannot be null or empty");
        DefaultToken defaultToken = (DefaultToken) jwtManagerService.extractJwtPayload(token, DefaultToken.class);
        Preconditions.checkNotNull(defaultToken, "Validate Refresh Token: Unable to extract jwt payload");
        tokenValidationService.verifyToken(defaultToken, List.of(TokenType.REFRESH.getType()));
        SimpleSymmetricCipher simpleSymmetricCipher = (new SimpleSymmetricCipherProvider()).get();
        RefreshToken refreshToken = new ObjectMapper().convertValue(new ObjectMapper().readTree(simpleSymmetricCipher.decrypt(defaultToken.getSecret())), RefreshToken.class);
        Preconditions.checkNotNull(refreshToken, "Validate Refresh Token: Unable to extract default token secret");
        Preconditions.checkArgument(null != refreshToken.getCustomerId() && !refreshToken.getCustomerId().isEmpty(), "Validate Refresh Token: customerId cannot be null or empty in the token");
        Preconditions.checkArgument(refreshToken.getCustomerId().equals(customerId), "Validate Refresh Token: requested and token customerId in the token not tally");
        //TODO: Upsert refresh token tab

    }
}
