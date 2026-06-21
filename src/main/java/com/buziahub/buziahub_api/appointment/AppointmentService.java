package com.buziahub.buziahub_api.appointment;

import com.buziahub.buziahub_api.appointment.dto.AppointmentResponse;
import com.buziahub.buziahub_api.appointment.dto.AppointmentSummary;
import com.buziahub.buziahub_api.appointment.dto.CreateAppointmentRequest;
import com.buziahub.buziahub_api.exceptions.PatientNotFoundException;
import com.buziahub.buziahub_api.patient.Patient;
import com.buziahub.buziahub_api.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new PatientNotFoundException(request.patientId()));

        Appointment appointment = Appointment.create(
                patient,
                request.startTime(),
                request.endTime()
        );

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return AppointmentResponse.from(savedAppointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentSummary> getAllAppointmentsSummary() {
        return appointmentRepository.findAll()
                .stream()
                .map(AppointmentSummary::from)
                .toList();
    }

    public List<AppointmentSummary> getAllUpcomingBookedAppointments() {
        return appointmentRepository.findByStatusAndStartTimeAfterOrderByStartTimeAsc(
                        AppointmentStatus.BOOKED,
                        LocalDateTime.now()
                )
                .stream()
                .map(AppointmentSummary::from)
                .toList();
    }

    public Optional<AppointmentSummary> getNextAppointment(Long patientId) {
        findPatientOrThrow(patientId);
        return appointmentRepository.findFirstByPatientIdAndStartTimeAfterOrderByStartTimeAsc(
                        patientId,
                        LocalDateTime.now()
                )
                .map(AppointmentSummary::from);
    }

    public List<AppointmentSummary> getFutureAppointments(Long patientId) {
        findPatientOrThrow(patientId);
        return appointmentRepository.findByPatientIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
                        patientId,
                        AppointmentStatus.BOOKED,
                        LocalDateTime.now()
                )
                .stream()
                .map(AppointmentSummary::from)
                .toList();
    }

    private Patient findPatientOrThrow(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));
    }

    /*
    getAppointmentById()
    cancelAppointment()*/


}
