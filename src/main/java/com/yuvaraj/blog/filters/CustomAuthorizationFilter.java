package com.yuvaraj.blog.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuvaraj.blog.helpers.ResponseHelper;
import com.yuvaraj.blog.services.SignInService;
import com.yuvaraj.security.helpers.JsonHelper;
import com.yuvaraj.security.models.AuthSuccessfulResponse;
import com.yuvaraj.security.services.JwtGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.yuvaraj.blog.helpers.Constants.SESSION_TOKEN_GENERATION_URL;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final SignInService signInService;
    private final JwtGenerationService jwtGenerationService;

    public CustomAuthorizationFilter(SignInService signInService, JwtGenerationService jwtGenerationService) {
        this.signInService = signInService;
        this.jwtGenerationService = jwtGenerationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        log.info("requesting to {}", servletPath);
        try {
            String authorization = request.getHeader(AUTHORIZATION);
            String customerId = request.getParameter("customerId");
            if (null != authorization && !authorization.isEmpty() && null != customerId && !customerId.isEmpty()) {
                signInService.validateSessionToken(authorization, customerId);
            } else if (servletPath.equals(SESSION_TOKEN_GENERATION_URL)) {
                response.setContentType(APPLICATION_JSON_VALUE);
                signInService.validateRefreshToken(authorization, customerId);
                AuthSuccessfulResponse authSuccessfulResponse = jwtGenerationService.generateSessionToken(customerId);
                log.info("{}", JsonHelper.toJson(authSuccessfulResponse));
                new ObjectMapper().writeValue(response.getOutputStream(), authSuccessfulResponse);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.info("Exception: Attempted to access authenticated url errorMessage={}, errorClass={}", e.getMessage(), e.getClass().getSimpleName());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            new ObjectMapper().writeValue(response.getOutputStream(), ResponseHelper.handleGeneralException(HttpStatus.FORBIDDEN.value(), null));
        }
    }
}
