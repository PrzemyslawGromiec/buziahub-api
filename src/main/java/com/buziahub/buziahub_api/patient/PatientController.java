package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.patient.dto.CreatePatientRequest;
import com.buziahub.buziahub_api.patient.dto.PatientResponse;
import com.buziahub.buziahub_api.patient.dto.PatientSearchCriteria;
import com.buziahub.buziahub_api.patient.dto.PatientSummaryResponse;
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


}
