package com.yuvaraj.blog.services;

import com.yuvaraj.blog.exceptions.signIn.SignInMaxSessionReachedException;
import com.yuvaraj.blog.models.inbuiltClass.CustomUser;
import com.yuvaraj.blog.models.signIn.SignInRequest;
import com.yuvaraj.security.models.AuthSuccessfulResponse;

public interface SignInService {

    void validateRefreshToken(String authorization, String customerId) throws Exception;

    void validateSessionToken(String authorization, String customerId) throws Exception;

    void handleSignInData(CustomUser user, AuthSuccessfulResponse authSuccessfulResponse, SignInRequest signInRequest) throws SignInMaxSessionReachedException;
}
