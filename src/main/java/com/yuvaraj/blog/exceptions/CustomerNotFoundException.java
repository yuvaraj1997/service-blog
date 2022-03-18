package com.yuvaraj.blog.exceptions;

import com.yuvaraj.blog.helpers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomerNotFoundException extends Exception {

    private final String errorMessage;
    private final ErrorCode errorCode;
}
