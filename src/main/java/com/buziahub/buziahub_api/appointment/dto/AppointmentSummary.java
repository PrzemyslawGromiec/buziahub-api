package com.buziahub.buziahub_api.appointment.dto;

import com.buziahub.buziahub_api.appointment.Appointment;
import com.buziahub.buziahub_api.appointment.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentSummary(
        Long id,
        Long patientId,
        String patientName,
        LocalDateTime startTime,
        AppointmentStatus status
) {

    public static AppointmentSummary from(
            Appointment appointment
    ) {
        return new AppointmentSummary(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getFirstName()
                + " " + appointment.getPatient().getLastName(),
                appointment.getStartTime(),
                appointment.getStatus()
        );
    }
}
