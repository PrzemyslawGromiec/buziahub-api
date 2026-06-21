package com.buziahub.buziahub_api.appointment;

import com.buziahub.buziahub_api.appointment.dto.AppointmentResponse;
import com.buziahub.buziahub_api.appointment.dto.AppointmentSummary;
import com.buziahub.buziahub_api.appointment.dto.CreateAppointmentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(request));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentSummary>> getAllAppointmentsSummary() {
        return ResponseEntity.ok(appointmentService.getAllAppointmentsSummary());
    }

    @GetMapping("/patients/{patientId}/next")
    public ResponseEntity<AppointmentSummary> getFutureAppointment(
            @PathVariable Long patientId
    ) {
        return appointmentService.getNextAppointment(patientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patients/{patientId}/future")
    public ResponseEntity<List<AppointmentSummary>> getAllFutureAppointments(
            @PathVariable Long patientId
    ) {
        return ResponseEntity.ok(appointmentService.getFutureAppointments(patientId));
    }




}
