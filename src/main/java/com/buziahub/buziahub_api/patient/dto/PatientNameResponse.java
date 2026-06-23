package com.buziahub.buziahub_api.patient.dto;

import com.buziahub.buziahub_api.patient.Patient;

public record PatientNameResponse(
        Long id,
        String fullName
) {

    public static PatientNameResponse from(Patient patient) {
        return new PatientNameResponse(
                patient.getId(),
                patient.getFirstName()
                        + " "
                        + patient.getLastName()
        );
    }
}
