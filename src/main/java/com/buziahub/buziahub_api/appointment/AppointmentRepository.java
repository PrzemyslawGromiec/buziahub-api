package com.buziahub.buziahub_api.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByStatusAndStartTimeAfterOrderByStartTimeAsc(
            AppointmentStatus status,
            LocalDateTime startTime
    );

    Optional<Appointment> findFirstByPatientIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
            Long patientId,
            AppointmentStatus status,
            LocalDateTime now
    );

    List<Appointment> findByPatientIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
            Long patientId,
            AppointmentStatus status,
            LocalDateTime now
    );

    List<Appointment> findByPatientIdAndEndTimeBeforeOrderByStartTimeDesc(
            Long patientId,
            LocalDateTime now
    );
}
