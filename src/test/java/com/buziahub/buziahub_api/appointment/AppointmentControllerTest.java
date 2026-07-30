package com.buziahub.buziahub_api.appointment;

import com.buziahub.buziahub_api.appointment.dto.AppointmentSummary;
import com.buziahub.buziahub_api.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    void shouldReturnEmptyListWhenPatientHasNoFutureAppointments() throws Exception {
        given(appointmentService.getFutureBookedAppointments(10006L))
                .willReturn(List.of());

        mockMvc.perform(get("/api/v1/appointments/patients/10006/future"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(appointmentService).getFutureBookedAppointments(10006L);
    }

    @Test
    void shouldReturnFutureAppointmentsForPatientWhenAppointmentsExist() throws Exception {

        LocalDateTime firstStart = LocalDateTime.of(2026, 8, 1, 10, 0);
        LocalDateTime secondStart = LocalDateTime.of(2026, 8, 8, 14, 30);

        AppointmentSummary firstAppointment = bookedAppointment(
                1L,
                firstStart
        );


        AppointmentSummary secondAppointment = bookedAppointment(
                2L,
                secondStart
        );

        given(appointmentService.getFutureBookedAppointments(10006L))
                .willReturn(List.of(firstAppointment, secondAppointment));

        mockMvc.perform(get("/api/v1/appointments/patients/10006/future"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].patientId").value(10006))
                .andExpect(jsonPath("$[0].patientName").value("John Smith"))
                .andExpect(jsonPath("$[0].startTime").value("2026-08-01T10:00:00"))
                .andExpect(jsonPath("$[0].status").value("BOOKED"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].patientId").value(10006))
                .andExpect(jsonPath("$[1].patientName").value("John Smith"))
                .andExpect(jsonPath("$[1].startTime").value("2026-08-08T14:30:00"))
                .andExpect(jsonPath("$[1].status").value("BOOKED"));

        verify(appointmentService).getFutureBookedAppointments(10006L);
    }

    @Test
    void shouldReturnBadRequestWhenPatientIdIsNotNumeric() throws Exception {

        mockMvc.perform(get("/api/v1/appointments/patients/abc/future"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Invalid Patient ID"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Patient ID is invalid"))
                .andExpect(jsonPath("$.errors.patientId").value("Invalid value: abc"));

        verifyNoInteractions(appointmentService);
    }

    private AppointmentSummary bookedAppointment(Long id, LocalDateTime startTime) {
        return new AppointmentSummary(
                id,
                10006L,
                "John Smith",
                startTime,
                AppointmentStatus.BOOKED
        );
    }


}
