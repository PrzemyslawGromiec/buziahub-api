package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.patient.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        return ResponseEntity
                .ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                patientService.getPatientById(id)
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<List<PatientSummaryResponse>> getAllPatientsSummary() {
        return ResponseEntity.ok(patientService.getAllPatientsSummary());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientSummaryResponse>> searchPatients(
            @ModelAttribute PatientSearchCriteria criteria) {
        return ResponseEntity.ok(patientService.searchPatients(criteria));
    }

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody CreatePatientRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(patientService.createPatient(request));
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<PatientResponse> updateName(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientNameRequest request
    ) {
        return ResponseEntity.ok(
                patientService.updatePatientName(id, request)
        );
    }

    @PatchMapping("/{id}/contact-details")
    public ResponseEntity<PatientResponse> updateContactDetails(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientContactDetails details
    ) {
        return ResponseEntity.ok(
                patientService.updateContactDetails(id, details)
        );
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<PatientResponse> archivePatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.archivePatient(id));
    }
}
