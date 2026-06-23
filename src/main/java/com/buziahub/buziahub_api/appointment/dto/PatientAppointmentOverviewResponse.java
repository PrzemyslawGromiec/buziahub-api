package com.buziahub.buziahub_api.appointment.dto;

import java.util.List;
import java.util.Optional;

public record PatientAppointmentOverviewResponse(
        Long patientId,
        String patientName,
        long totalAppointments,
        long bookedAppointments,
        long completedAppointments,
        long cancelledAppointments,
        long unpaidAppointments,
        Optional<AppointmentSummary> nextAppointment,
        List<AppointmentSummary> lastThreeAppointments
) {
}