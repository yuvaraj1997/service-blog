package com.yuvaraj.blog.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuvaraj.blog.helpers.ResponseHelper;
import com.yuvaraj.blog.models.inbuiltClass.CustomUser;
import com.yuvaraj.security.helpers.JsonHelper;
import com.yuvaraj.security.models.AuthSuccessfulResponse;
import com.yuvaraj.security.services.JwtGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static com.yuvaraj.blog.helpers.ErrorCode.INVALID_USERNAME_OR_PASSWORD;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtGenerationService jwtGenerationService;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JwtGenerationService jwtGenerationService) {
        super(new AntPathRequestMatcher("/api/login", "POST"));
        this.authenticationManager = authenticationManager;
        this.jwtGenerationService = jwtGenerationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email, password;
        Map requestMap = null;
        try {
            requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            email = requestMap.get("email").toString();
            password = requestMap.get("password").toString();
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        log.info("[{}]:AttemptAuthentication: Attempting login", email);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON_VALUE);
        CustomUser user = (CustomUser) authResult.getPrincipal();
        try {
            //TODO: Enhance security service to return all possible values like
            AuthSuccessfulResponse authSuccessfulResponse = jwtGenerationService.generateRefreshToken(user.getCustomerId());
            log.info("{}", JsonHelper.toJson(authSuccessfulResponse));
            new ObjectMapper().writeValue(response.getOutputStream(), authSuccessfulResponse);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        //TODO: Can handle locking system
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        new ObjectMapper().writeValue(response.getOutputStream(), ResponseHelper.handleGeneralException(HttpStatus.BAD_REQUEST.value(), INVALID_USERNAME_OR_PASSWORD));
    }
}
