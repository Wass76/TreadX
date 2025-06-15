package com.TreadX.utils.restExceptionHanding;

import com.TreadX.utils.exception.*;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class AbstractRestHandler {

    @ExceptionHandler(value = {RequestNotValidException.class})
    public ResponseEntity<Object> handleRequestNotValidException(RequestNotValidException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(
                e.getMessage(),
                badRequest,
                LocalDateTime.now());
        return new ResponseEntity<>(apiException,badRequest);
    }
    @ExceptionHandler(value = {ObjectNotValidException.class})
    public ResponseEntity<Object> handleObjectNotValidException(ObjectNotValidException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(
                e.getErrormessage().toString(),
                badRequest,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException,badRequest);
    }

    @ExceptionHandler(value = {TooManyRequestException.class})
    public ResponseEntity<Object> handleTooManyRequestException(TooManyRequestException e){
        HttpStatus tooManyRequests = HttpStatus.TOO_MANY_REQUESTS;
        ApiException apiException = new ApiException(
                e.getMessage(),
                tooManyRequests,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException,tooManyRequests);
    }

    @ExceptionHandler(value = {UnAuthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(UnAuthorizedException e){
        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        ApiException apiException = new ApiException(
                e.getMessage(),
                unauthorized,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException,unauthorized);
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException e){
        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        ApiException apiException = new ApiException(
                e.getMessage(),
                unauthorized,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException,unauthorized);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e){
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ApiException apiException = new ApiException(
                e.getMessage(),
                notFound,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException,notFound);
    }

    @ExceptionHandler(value = {ConflictException.class})
    public ResponseEntity<Object> handleConflictException(ConflictException e){
        HttpStatus conflict = HttpStatus.CONFLICT;
        ApiException apiException = new ApiException(
                e.getMessage(),
                conflict,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException,conflict);
    }

    @ExceptionHandler(value = {InvalidStatusTransitionException.class})
    public ResponseEntity<Object> handleInvalidStatusTransitionException(InvalidStatusTransitionException e){
        HttpStatus invalidStatus = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(
                e.getMessage(),
                invalidStatus,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException,invalidStatus);
    }
}
