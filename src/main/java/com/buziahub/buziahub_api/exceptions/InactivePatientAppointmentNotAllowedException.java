package com.buziahub.buziahub_api.exceptions;

public class InactivePatientAppointmentNotAllowedException extends RuntimeException {
    public InactivePatientAppointmentNotAllowedException(Long patientId) {
        super("Cannot create an appointment for an inactive patient with ID: " + patientId);
    }
}
