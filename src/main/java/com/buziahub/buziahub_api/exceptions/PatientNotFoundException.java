package com.buziahub.buziahub_api.exceptions;

public class PatientNotFoundException extends RuntimeException{
    public PatientNotFoundException(Long id) {
        super("Patient with id " + id + " not found");
    }
}
