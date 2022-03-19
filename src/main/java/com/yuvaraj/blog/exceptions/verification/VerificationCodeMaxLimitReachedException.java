package com.yuvaraj.blog.exceptions.verification;

import com.yuvaraj.blog.helpers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VerificationCodeMaxLimitReachedException extends Exception {

    private final String errorMessage;
    private final ErrorCode errorCode;
}
