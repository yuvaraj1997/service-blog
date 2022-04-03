package com.yuvaraj.blog.services;

public interface SignInService {

    void validateRefreshToken(String token, String customerId) throws Exception;
}
