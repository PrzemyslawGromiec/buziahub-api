package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.exceptions.PatientNotFoundException;
import com.buziahub.buziahub_api.patient.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatientsIncludingArchived() {
        return patientRepository.findAll()
                .stream()
                .map(PatientResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getActivePatients() {
        return patientRepository.findByActiveTrue()
                .stream()
                .map(PatientResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PatientSummaryResponse> getAllPatientsSummary() {
        return patientRepository.findAll()
                .stream()
                .map(PatientSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        Patient patient = findPatientOrThrow(id);
        return PatientResponse.from(patient);
    }

    public PatientResponse archivePatient(Long id) {
        Patient patient = findPatientOrThrow(id);
        patient.archive();
        return PatientResponse.from(patient);
    }

    public List<PatientNameResponse> getPatientNames() {
        return patientRepository.findByActiveTrue()
                .stream()
                .map(PatientNameResponse::from)
                .toList();
    }

    @Transactional
    public long deleteAllPatients() {
        long count = patientRepository.count();
        patientRepository.deleteAll();
        logger.warn("All {} patients deleted", count);
        return count;
    }

    private Patient findPatientOrThrow(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));
    }

}
