package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.exceptions.PatientNotFoundException;
import com.buziahub.buziahub_api.patient.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(PatientResponse::from)
                .toList();
    }

    public List<PatientSummaryResponse> getAllPatientsSummary() {
        return patientRepository.findAll()
                .stream()
                .map(PatientSummaryResponse::from)
                .toList();
    }

    public List<PatientSummaryResponse> searchPatients(PatientSearchCriteria criteria) {
        return patientRepository
                .findAll(PatientSpecification.withCriteria(criteria))
                .stream()
                .map(PatientSummaryResponse::from)
                .toList();
    }

    public PatientResponse createPatient(CreatePatientRequest request) {
        Patient patient = Patient.create(
                request.firstName(),
                request.lastName(),
                request.dateOfBirth(),
                request.gender(),
                request.address(),
                request.phoneNumber(),
                request.emergencyContact(),
                request.comments()
        );

        Patient savedPatient = patientRepository.save(patient);
        return PatientResponse.from(savedPatient);
    }

    @Transactional
    public PatientResponse updatePatientName(
            Long patientId,
            UpdatePatientNameRequest request
    ) {
        Patient patient = findPatientOrThrow(patientId);

        patient.updateName(
                request.firstName(),
                request.lastName()
        );
        return PatientResponse.from(patient);
    }

    @Transactional
    public PatientResponse updateContactDetails(
            Long patientId,
            UpdatePatientContactDetails details
    ) {
        Patient patient = findPatientOrThrow(patientId);

        patient.updateContactDetails(
                details.address(),
                details.phoneNumber(),
                details.emergencyContact()
        );

        return PatientResponse.from(patient);
    }

    public PatientResponse getPatientById(Long id) {
        Patient patient = findPatientOrThrow(id);
        return PatientResponse.from(patient);
    }

    public PatientResponse archivePatient(Long id) {
        Patient patient = findPatientOrThrow(id);
        patient.archive();
        return PatientResponse.from(patient);
    }

    private Patient findPatientOrThrow(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));
    }

}
