package com.buziahub.buziahub_api.appointment.dto;

import com.buziahub.buziahub_api.appointment.Appointment;
import com.buziahub.buziahub_api.appointment.AppointmentStatus;
import com.buziahub.buziahub_api.appointment.PaymentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        PaymentStatus paymentStatus
) {

    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                appointment.getPaymentStatus()
        );
    }
}
