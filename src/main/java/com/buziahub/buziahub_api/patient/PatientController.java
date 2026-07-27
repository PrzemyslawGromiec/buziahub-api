package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.patient.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getActivePatients() {
        return ResponseEntity.ok(patientService.getActivePatients());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<PatientResponse>> getAllPatientsIncludingArchived() {
        return ResponseEntity.ok(patientService.getAllPatientsIncludingArchived());
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

    @GetMapping("/search/paginated")
    public ResponseEntity<Page<PatientSummaryResponse>> searchPatients(
            @ModelAttribute PatientSearchCriteria criteria,
            @PageableDefault(page = 0, size = 10, sort = "lastName", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(patientService.searchPatients(criteria, pageable));
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

    @GetMapping("/names")
    public ResponseEntity<List<PatientNameResponse>> getAllPatientNames() {
        return ResponseEntity.ok(patientService.getPatientNames());
    }

    @GetMapping("/recent")
    public ResponseEntity<Page<RecentPatientResponse>> getRecentPatients(
            @PageableDefault(page = 0, size = 10)
            Pageable pageable
    ) {
        return ResponseEntity.ok(patientService.getRecentPatients(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/all")
    public ResponseEntity<DeleteResponse> deleteAll() {
        long deletedCount = patientService.deleteAllPatients();
        return ResponseEntity.ok(
                new DeleteResponse(
                        "All patients deleted successfully",
                        deletedCount,
                        System.currentTimeMillis()
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/seeded")
    public ResponseEntity<DeleteResponse> deleteSeeded() {
        long deletedCount = patientService.deleteSeededPatients();
        return ResponseEntity.ok(
                new DeleteResponse(
                        "Seeded patients deleted successfully",
                        deletedCount,
                        System.currentTimeMillis()
                )
        );
    }
}
