package com.buziahub.buziahub_api.appointment;

import com.buziahub.buziahub_api.patient.Patient;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static Appointment create(
            Patient patient,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return Appointment.builder()
                .patient(patient)
                .startTime(startTime)
                .endTime(endTime)
                .status(AppointmentStatus.BOOKED)
                .paymentStatus(PaymentStatus.UNPAID)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void cancel() {
        this.status = AppointmentStatus.CANCELLED;
    }

    public void complete() {
        this.status = AppointmentStatus.COMPLETED;
    }

    public void markAsPaid() {
        this.paymentStatus = PaymentStatus.PAID;
    }
}
