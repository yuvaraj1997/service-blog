package com.yuvaraj.blog.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(1000, "Internal Server Error"),
    INVALID_ARGUMENT(1004, "Invalid Argument"),
    CUSTOMER_NOT_FOUND(2000, "Customer Not Found"),
    CUSTOMER_ALREADY_EXIST(2001, "Customer already exist.");

    private final int code;
    private final String message;
}
