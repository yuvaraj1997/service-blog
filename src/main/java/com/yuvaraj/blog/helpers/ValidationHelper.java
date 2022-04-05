package com.yuvaraj.blog.helpers;

import com.yuvaraj.blog.exceptions.InvalidArgumentException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationHelper {

    public static void checkNotNullAndNotEmpty(String value, String errorMessage) throws InvalidArgumentException {
        if (null == value || value.isEmpty()) {
            throw new InvalidArgumentException(errorMessage, ErrorCode.INVALID_ARGUMENT);
        }
    }
}
