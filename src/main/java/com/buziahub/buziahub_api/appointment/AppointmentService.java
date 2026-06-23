package com.buziahub.buziahub_api.appointment;

import com.buziahub.buziahub_api.appointment.dto.AppointmentResponse;
import com.buziahub.buziahub_api.appointment.dto.AppointmentSummary;
import com.buziahub.buziahub_api.appointment.dto.CreateAppointmentRequest;
import com.buziahub.buziahub_api.appointment.dto.PatientAppointmentOverviewResponse;
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

    @Transactional(readOnly = true)
    public List<AppointmentSummary> getAllUpcomingBookedAppointments() {
        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository.findByStatusAndStartTimeAfterOrderByStartTimeAsc(
                        AppointmentStatus.BOOKED,
                        now
                )
                .stream()
                .map(AppointmentSummary::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<AppointmentSummary> getNextAppointment(Long patientId) {
        findPatientOrThrow(patientId);
        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository.findFirstByPatientIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
                        patientId,
                        AppointmentStatus.BOOKED,
                        now
                )
                .map(AppointmentSummary::from);
    }

    @Transactional(readOnly = true)
    public List<AppointmentSummary> getFutureBookedAppointments(Long patientId) {
        findPatientOrThrow(patientId);
        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository.findByPatientIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
                        patientId,
                        AppointmentStatus.BOOKED,
                        now
                )
                .stream()
                .map(AppointmentSummary::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentSummary> getPastAppointments(Long patientId) {
        findPatientOrThrow(patientId);
        return appointmentRepository.findByPatientIdAndEndTimeBeforeOrderByStartTimeDesc(
                        patientId,
                        LocalDateTime.now()
                )
                .stream()
                .map(AppointmentSummary::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PatientAppointmentOverviewResponse getPatientAppointmentsOverview(
            Long patientId
    ) {
        Patient patient = findPatientOrThrow(patientId);
        LocalDateTime now = LocalDateTime.now();

        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        String patientName = patient.getFirstName() + " " + patient.getLastName();

        long totalAppointments = appointments.size();
        long bookedAppointments = appointments
                .stream()
                .filter(a -> a.getStatus() == AppointmentStatus.BOOKED)
                .count();

        long completedAppointments = appointments
                .stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .count();


        long cancelledAppointments = appointments
                .stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED)
                .count();

        long unpaidAppointments = appointments
                .stream()
                .filter(a -> a.getPaymentStatus() == PaymentStatus.UNPAID)
                .count();

        Optional<AppointmentSummary> nextAppointment = appointments.stream()
                .filter(a -> a.getStartTime().isAfter(now))
                .filter(a -> a.getStatus() == AppointmentStatus.BOOKED)
                .sorted(Comparator.comparing(Appointment::getStartTime))
                .findFirst()
                .map(AppointmentSummary::from);

        List<AppointmentSummary> lastThreeAppointments = appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .filter(a -> a.getEndTime().isBefore(now))
                .sorted(Comparator.comparing(Appointment::getEndTime).reversed())
                .limit(3)
                .map(AppointmentSummary::from)
                .toList();

        return new PatientAppointmentOverviewResponse(
                patientId,
                patientName,
                totalAppointments,
                bookedAppointments,
                completedAppointments,
                cancelledAppointments,
                unpaidAppointments,
                nextAppointment,
                lastThreeAppointments
        );
    }

    private Patient findPatientOrThrow(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));
    }

    /*
    getAppointmentById()
    cancelAppointment()*/


}
