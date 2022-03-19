package com.yuvaraj.blog.configuration;

import com.yuvaraj.blog.exceptions.CustomerNotFoundException;
import com.yuvaraj.blog.exceptions.signup.CustomerAlreadyExistException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeMaxLimitReachedException;
import com.yuvaraj.blog.exceptions.verification.VerificationCodeResendNotAllowedException;
import com.yuvaraj.blog.helpers.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static com.yuvaraj.blog.helpers.ResponseHelper.handleGeneralException;
import static com.yuvaraj.blog.helpers.ResponseHelper.handleMethodArgumentNotValidException;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerConfiguration extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<Object> handleSpecialException(Exception exception, WebRequest request, HttpServletRequest httpRequest) {
        String logMessage = String.format("%s %s", httpRequest.getMethod(), httpRequest.getRequestURI());
        log.error("{}: path = {} errorMessage={}", exception.getClass().getSimpleName(), logMessage, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        if (exception instanceof RuntimeException) {
            return handleExceptionInternal(exception, handleGeneralException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.INTERNAL_SERVER_ERROR), headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
        return handleExceptionInternal(exception, handleGeneralException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.INTERNAL_SERVER_ERROR), headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, handleMethodArgumentNotValidException(HttpStatus.BAD_REQUEST.value(), ErrorCode.INVALID_ARGUMENT, ex), headers, status, request);
    }

    @ExceptionHandler({CustomerNotFoundException.class})
    public ResponseEntity<Object> customerNotFoundException(CustomerNotFoundException customerNotFoundException, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        return handleExceptionInternal(customerNotFoundException, handleGeneralException(HttpStatus.BAD_REQUEST.value(), customerNotFoundException.getErrorCode()), headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({CustomerAlreadyExistException.class})
    public ResponseEntity<Object> customerAlreadyExistException(CustomerAlreadyExistException customerAlreadyExistException, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        return handleExceptionInternal(customerAlreadyExistException, handleGeneralException(HttpStatus.BAD_REQUEST.value(), customerAlreadyExistException.getErrorCode()), headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({VerificationCodeMaxLimitReachedException.class})
    public ResponseEntity<Object> verificationCodeMaxLimitReachedExceptionException(VerificationCodeMaxLimitReachedException verificationCodeMaxLimitReachedException, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        return handleExceptionInternal(verificationCodeMaxLimitReachedException, handleGeneralException(HttpStatus.BAD_REQUEST.value(), verificationCodeMaxLimitReachedException.getErrorCode()), headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({VerificationCodeResendNotAllowedException.class})
    public ResponseEntity<Object> verificationCodeMaxLimitReachedExceptionException(VerificationCodeResendNotAllowedException verificationCodeResendNotAllowedException, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        return handleExceptionInternal(verificationCodeResendNotAllowedException, handleGeneralException(HttpStatus.BAD_REQUEST.value(), verificationCodeResendNotAllowedException.getErrorCode()), headers, HttpStatus.BAD_REQUEST, request);
    }


}
