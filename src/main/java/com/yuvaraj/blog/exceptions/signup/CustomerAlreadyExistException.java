package com.yuvaraj.blog.exceptions.signup;

import com.yuvaraj.blog.helpers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomerAlreadyExistException extends Exception {

    private final String errorMessage;
    private final ErrorCode errorCode;
}
