package com.buziahub.buziahub_api.appointment.dto;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(
        Long patientId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
