package com.yuvaraj.blog.helpers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHelper {

    static Map<Integer, String> httpStatusCode = new HashMap<>();

    static {
        httpStatusCode.put(HttpStatus.OK.value(), "HTTP 200 - OK");
        httpStatusCode.put(HttpStatus.BAD_REQUEST.value(), "HTTP 400 - Invalid Request Message");
        httpStatusCode.put(HttpStatus.INTERNAL_SERVER_ERROR.value(), "HTTP 500 - Internal Service Error");
    }

    public static Object handleGeneralException(Integer statusCode, ErrorCode errorCode) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", getStatusNodeMapping(statusCode));
        if (null != errorCode) {
            response.put("error", getErrorNodeMapping(errorCode));
        }
        return response;
    }

    public static Map<String, Object> handleMethodArgumentNotValidException(Integer statusCode, ErrorCode errorCode, MethodArgumentNotValidException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", getStatusNodeMapping(statusCode));
        if (null != errorCode) {
            response.put("error", getErrorNodeMapping(errorCode));
        }
        Map<String, Object> additionalProperties = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            additionalProperties.put(fieldName, errorMessage);
        });
        response.put("additionalProperties", additionalProperties);
        return response;
    }

    private static Map<String, Object> getStatusNodeMapping(Integer statusCode) {
        Map<String, Object> statusNode = new LinkedHashMap<>();
        statusNode.put("code", statusCode);
        statusNode.put("message", httpStatusCode.get(statusCode));
        return statusNode;
    }

    private static Object getErrorNodeMapping(ErrorCode errorCode) {
        Map<String, Object> statusNode = new LinkedHashMap<>();
        statusNode.put("code", errorCode.getCode());
        statusNode.put("message", errorCode.getMessage());
        return statusNode;
    }

    public static ResponseEntity badAsJson(ErrorCode errorCode) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", getStatusNodeMapping(HttpStatus.BAD_REQUEST.value()));
        if (null != errorCode) {
            response.put("error", getErrorNodeMapping(errorCode));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

    public static ResponseEntity ok(Object object) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(object);
    }
}
