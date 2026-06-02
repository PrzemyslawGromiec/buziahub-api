package com.buziahub.buziahub_api.patient.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePatientContactDetails(
        @Size(max = 255)
        String address,

        @Pattern(
                regexp = "^\\+[1-9]\\d{7,14}$",
                message = "Phone number must be in international format, e.g. +447766885522"
        )
        String phoneNumber,

        @Pattern(
                regexp = "^\\+[1-9]\\d{7,14}$",
                message = "Emergency contact must be in international format"
        )
        String emergencyContact
) {
}
