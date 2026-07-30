package com.buziahub.buziahub_api.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPatientSearchCriteriaException.class)
    public ProblemDetail handleInvalidPatientSearchCriteriaException(InvalidPatientSearchCriteriaException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid Patient Search Criteria");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ProblemDetail handlePatientNotFoundException(PatientNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Patient Not Found");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ProblemDetail handleAppointmentNotFoundException(AppointmentNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Appointment Not Found");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(InvalidAppointmentTimeRangeException.class)
    public ProblemDetail handleInvalidAppointmentTimeRangeException(InvalidAppointmentTimeRangeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid Appointment Time Range");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(InactivePatientAppointmentNotAllowedException.class)
    public ProblemDetail handleInactivePatientAppointmentNotAllowedException(InactivePatientAppointmentNotAllowedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Inactive Patient Appointment");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(OverlappingAppointmentException.class)
    public ProblemDetail handleOverlappingAppointmentException(OverlappingAppointmentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Overlapping Patient Appointment");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail("Request body contains invalid or missing values");

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> {
                            String message = error.getDefaultMessage();
                            return (message != null) ? message : "Invalid value";
                        },
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        problemDetail.setProperty("fieldErrors", fieldErrors);
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Constraint Violation");
        problemDetail.setDetail("One or more request constraints were violated.");

        Map<String, String> violations = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        problemDetail.setProperty("errors", violations);
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Malformed Request Body");
        problemDetail.setDetail("Request body is malformed or contains invalid value types.");
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid Request Parameter");
        problemDetail.setDetail("Request body contains invalid or missing values");
        String parameterName = ex.getName();
        Object invalidValue = ex.getValue();

        if ("patientId".equals(parameterName)) {
            problemDetail.setTitle("Invalid Patient ID");
            problemDetail.setDetail("Patient ID is invalid");
        }

        Map<String, String> errorDetails = new LinkedHashMap<>();
        errorDetails.put(parameterName, "Invalid value: " + invalidValue);
        problemDetail.setProperty("errors", errorDetails);
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail("An unexpected error occurred.");
        return problemDetail;
    }
}
