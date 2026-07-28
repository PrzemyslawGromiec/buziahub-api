package com.buziahub.buziahub_api.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
                SELECT (COUNT(a) > 0)
                FROM Appointment a
                WHERE a.patient.id = :patientId
                    AND a.status = :status
                    AND a.startTime < :newEnd
                    AND a.endTime > :newStart
            """)
    boolean existsOverlappingAppointment(
            @Param("patientId") Long patientId,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd,
            @Param("status") AppointmentStatus status
    );
}
