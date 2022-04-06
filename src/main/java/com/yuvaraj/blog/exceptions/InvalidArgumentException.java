package com.yuvaraj.blog.exceptions;

import com.yuvaraj.blog.helpers.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidArgumentException extends CustomException {

    private final String errorMessage;
    private final ErrorCode errorCode;

    public InvalidArgumentException(String errorMessage, ErrorCode errorCode) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public InvalidArgumentException(String message, String errorMessage, ErrorCode errorCode) {
        super(message);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
