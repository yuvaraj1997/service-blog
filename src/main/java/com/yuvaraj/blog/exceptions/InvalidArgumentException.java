package com.yuvaraj.blog.exceptions;

import com.yuvaraj.blog.helpers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvalidArgumentException extends CustomException {

    private final String errorMessage;
    private final ErrorCode errorCode;
}
