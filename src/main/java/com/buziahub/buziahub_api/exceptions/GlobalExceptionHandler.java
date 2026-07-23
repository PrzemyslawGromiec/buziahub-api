package com.buziahub.buziahub_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPatientSearchCriteriaException.class)
    public ProblemDetail handleInvalidPatientSearchCriteriaException(InvalidPatientSearchCriteriaException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid Patient Search Criteria");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }
}
