package com.buziahub.buziahub_api.patient.dto;

import com.buziahub.buziahub_api.patient.Patient;

public record PatientSummaryResponse(
        String firstName,
        String lastName,
        String phoneNumber
) {
    public static PatientSummaryResponse from(Patient patient) {
        return new PatientSummaryResponse(
                patient.getFirstName(),
                patient.getLastName(),
                patient.getPhoneNumber()
        );
    }
}
