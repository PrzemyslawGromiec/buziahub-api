package com.buziahub.buziahub_api.appointment;

import com.buziahub.buziahub_api.appointment.dto.AppointmentResponse;
import com.buziahub.buziahub_api.appointment.dto.CreateAppointmentRequest;
import com.buziahub.buziahub_api.appointment.dto.PatientAppointmentOverviewResponse;
import com.buziahub.buziahub_api.exceptions.PatientNotFoundException;
import com.buziahub.buziahub_api.patient.Patient;
import com.buziahub.buziahub_api.patient.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void createAppointment_shouldCreateAppointmentWhenPatientExists() {

        Patient patient = Patient.create(
                "John",
                "Snow",
                null,
                null,
                "London",
                "+447756467364",
                null,
                null
        );

        CreateAppointmentRequest request = new CreateAppointmentRequest(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(45)
        );

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response = appointmentService.createAppointment(request);

        verify(patientRepository).findById(1L);
        verify(appointmentRepository).save(any(Appointment.class));

        assertEquals(request.startTime(), response.startTime());
        assertEquals(request.endTime(), response.endTime());
    }

    @Test
    void createAppointment_shouldThrowExceptionWhenPatientDoesNotExists() {
        // given
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                99L,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(45)
        );

        when(patientRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                PatientNotFoundException.class,
                () -> appointmentService.createAppointment(request)
        );

        verify(patientRepository).findById(99L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void getPatientAppointmentsOverview_shouldReturnOverviewWhenPatientExists() {

        Patient patient = Patient.create(
                "John",
                "Snow",
                null,
                null,
                "London",
                "+447756467364",
                null,
                null
        );

        LocalDateTime now = LocalDateTime.now();

        Appointment bookedFuture = Appointment.create(
                patient,
                now.plusDays(1),
                now.plusDays(1).plusMinutes(45)
        );

        Appointment completedPast = Appointment.create(
                patient,
                LocalDateTime.of(2026,4,12,12,00),
                LocalDateTime.of(2026,4,12,12,45)
        );

        completedPast.complete();


        Appointment cancelledAppointment = Appointment.create(
                patient,
                LocalDateTime.of(2026,4,13,12,00),
                LocalDateTime.of(2026,4,13,12,45)
        );
        cancelledAppointment.cancel();

        Appointment bookedPaidPast = Appointment.create(
                patient,
                LocalDateTime.of(2026,5,12,12,00),
                LocalDateTime.of(2026,5,12,12,45)
        );

        bookedPaidPast.markAsPaid();

        List<Appointment> appointments = List.of(
                bookedFuture,
                completedPast,
                cancelledAppointment,
                bookedPaidPast
        );

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(appointmentRepository.findByPatientId(1L))
                .thenReturn(appointments);

        PatientAppointmentOverviewResponse response =
                appointmentService.getPatientAppointmentsOverview(1L);

        assertEquals(1L, response.patientId());
        assertEquals("John Snow", response.patientName());

        assertEquals(4, response.totalAppointments());
        assertEquals(2, response.bookedAppointments());
        assertEquals(1, response.completedAppointments());
        assertEquals(1, response.cancelledAppointments());
        assertEquals(3, response.unpaidAppointments());

        assertTrue(response.nextAppointment().isPresent());
        assertEquals(bookedFuture.getStartTime(), response.nextAppointment().get().startTime());

        assertEquals(1, response.lastThreeAppointments().size());
        assertEquals(completedPast.getStartTime(), response.lastThreeAppointments().get(0).startTime());

        verify(patientRepository).findById(1L);
        verify(appointmentRepository).findByPatientId(1L);
    }
}
