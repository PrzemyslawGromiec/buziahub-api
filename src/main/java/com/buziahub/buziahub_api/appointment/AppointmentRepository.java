package com.buziahub.buziahub_api.appointment;

import com.buziahub.buziahub_api.appointment.dto.AppointmentSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByStatusAndStartTimeAfterOrderByStartTimeAsc(
            AppointmentStatus status,
            LocalDateTime startTime
    );

    Optional<Appointment> findFirstByPatientIdAndStartTimeAfterOrderByStartTimeAsc(
            Long patientId,
            LocalDateTime now
    );

    List<Appointment> findByPatientIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
            Long patientId,
            AppointmentStatus status,
            LocalDateTime now
    );
}
