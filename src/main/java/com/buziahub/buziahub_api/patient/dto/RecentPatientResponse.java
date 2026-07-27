package com.buziahub.buziahub_api.patient.dto;

import java.time.LocalDateTime;

public record RecentPatientResponse(
        Long id,
        String firstName,
        String lastName,
        LocalDateTime createdAt
) {
}
