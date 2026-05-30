package com.buziahub.buziahub_api.patient.dto;

import com.buziahub.buziahub_api.common.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreatePatientRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 50)
        @Pattern(
                regexp = "^[\\p{L}' -]+$",
                message = "First name contains invalid characters"
        )
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50)
        @Pattern(
                regexp = "^[\\p{L}' -]+$",
                message = "Last name contains invalid characters"
        )
        String lastName,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotNull(message = "Gender is required")
        Gender gender,

        @Size(max = 255, message = "Address must be at most 255 characters")
        String address,
        @Pattern(
                regexp = "^\\+[1-9]\\d{7,14}$",
                message = "Phone number must be in international format, e.g. +447766885522"
        )
        String phoneNumber,

        @Pattern(
                regexp = "^\\+[1-9]\\d{7,14}$",
                message = "Phone number must be in international format, e.g. +447766885522"
        )
        String emergencyContact,
        String comments
        ) {
}
