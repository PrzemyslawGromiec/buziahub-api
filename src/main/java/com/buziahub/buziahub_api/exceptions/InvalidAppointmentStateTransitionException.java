package com.buziahub.buziahub_api.exceptions;

public class InvalidAppointmentStateTransitionException extends RuntimeException {
    public InvalidAppointmentStateTransitionException(String message) {
        super(message);
    }
}
