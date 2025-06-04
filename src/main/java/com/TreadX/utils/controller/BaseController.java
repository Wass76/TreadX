package com.TreadX.utils.controller;

import com.TreadX.utils.response.ApiResponseClass;
import com.TreadX.utils.response.PaginationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin("*")
public class BaseController {

    public ResponseEntity<?> sendResponse(Object response, String message , HttpStatus status, PaginationResponse paginationResponse) {

        ApiResponseClass apiResponseClass =
                ApiResponseClass.builder()
                        .pageable(paginationResponse)
                        .message(message)
                        .status(status)
                        .localDateTime(LocalDateTime.now())
                        .body(response)
                        .build();
        return new ResponseEntity<>(apiResponseClass, status);
    }
    public ResponseEntity<?> sendResponse(Object response, String message , HttpStatus status) {

        ApiResponseClass apiResponseClass =
                ApiResponseClass.builder()
                        .message(message)
                        .status(status)
                        .localDateTime(LocalDateTime.now())
                        .body(response)
                        .build();
        return new ResponseEntity<>(apiResponseClass, status);
    }

    public ResponseEntity<?> sendResponse( String message , HttpStatus status) {
        ApiResponseClass apiResponseClass =
                ApiResponseClass.builder()
                        .message(message)
                        .status(status)
                        .localDateTime(LocalDateTime.now())
                        .build();
        return new ResponseEntity<>(apiResponseClass, status);
    }
}
