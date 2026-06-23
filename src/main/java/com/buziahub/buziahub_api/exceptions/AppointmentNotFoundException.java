package com.buziahub.buziahub_api.exceptions;

public class AppointmentNotFoundException extends RuntimeException{
    public AppointmentNotFoundException(Long id) {
        super("Appointment with id " + id + " not found");
    }
}
