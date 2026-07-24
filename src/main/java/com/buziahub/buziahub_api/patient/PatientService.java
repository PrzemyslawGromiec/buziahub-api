package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.exceptions.InvalidPatientSearchCriteriaException;
import com.buziahub.buziahub_api.exceptions.PatientNotFoundException;
import com.buziahub.buziahub_api.patient.dto.*;
import com.buziahub.buziahub_api.patient.seed.PatientSeedConstants;
import com.buziahub.buziahub_api.patient.seed.PatientSeedDataRunner;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return patientRepository.findAllSummaries();
    }

    @Transactional(readOnly = true)
    public List<PatientSummaryResponse> searchPatients(PatientSearchCriteria criteria) {
        return patientRepository
                .findAll(PatientSpecification.withCriteria(criteria))
                .stream()
                .map(PatientSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<PatientSummaryResponse> searchPatients(PatientSearchCriteria criteria, Pageable pageable) {
        validateSearchCriteria(criteria);
        return patientRepository
                .findAll(PatientSpecification.withCriteria(criteria), pageable)
                .map(PatientSummaryResponse::from);
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
        return patientRepository.findActivePatientNames();
    }

    @Transactional
    public long deleteAllPatients() {
        long count = patientRepository.count();
        patientRepository.deleteAllInBatch();
        logger.warn("All {} patients deleted", count);
        return count;
    }

    @Transactional
    public long deleteSeededPatients() {
        return patientRepository.deleteByCommentsStartingWith(PatientSeedConstants.MARKER);
    }

    private Patient findPatientOrThrow(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));
    }

    private void validateSearchCriteria(PatientSearchCriteria criteria) {
        TextMatchMode mode = criteria.matchMode() == null ? TextMatchMode.PREFIX : criteria.matchMode();
        if (mode == TextMatchMode.CONTAINS) {
            boolean firstNameTooShort = criteria.firstName() != null
                    && !criteria.firstName().isBlank()
                    && criteria.firstName().trim().length() < 2;

            boolean lastNameTooShort = criteria.lastName() != null
                    && !criteria.lastName().isBlank()
                    && criteria.lastName().trim().length() < 2;

            if (firstNameTooShort || lastNameTooShort) {
                throw new InvalidPatientSearchCriteriaException(
                        "CONTAINS mode requires at least 2 characters for firstName and lastName"
                );
            }
        }
    }


}
