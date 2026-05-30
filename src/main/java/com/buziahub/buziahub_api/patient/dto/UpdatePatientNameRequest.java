package com.buziahub.buziahub_api.patient.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePatientNameRequest(
        @Size(
                max = 50,
                message = "First name cannot exceed 50 characters"
        )
        @Pattern(
                regexp = "^[\\p{L}' -]+$",
                message = "First name contains invalid characters"
        )
        String firstName,

        @Size(
                max = 50,
                message = "Last name cannot exceed 50 characters"
        )
        @Pattern(
                regexp = "^[\\p{L}' -]+$",
                message = "Last name contains invalid characters"
        )
        String lastName
) {
}
