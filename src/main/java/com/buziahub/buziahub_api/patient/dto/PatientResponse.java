package com.buziahub.buziahub_api.patient.dto;

import com.buziahub.buziahub_api.common.Gender;
import com.buziahub.buziahub_api.patient.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponse(
        Long id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Gender gender,
        String address,
        String phoneNumber,
        String emergencyContact,
        String comments,
        LocalDateTime createdAt,
        Boolean active
) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getAddress(),
                patient.getPhoneNumber(),
                patient.getEmergencyContact(),
                patient.getComments(),
                patient.getCreatedAt(),
                patient.getActive()
        );
    }
}
