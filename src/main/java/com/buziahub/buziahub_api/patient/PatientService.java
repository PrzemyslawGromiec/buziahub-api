package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.patient.dto.CreatePatientRequest;
import com.buziahub.buziahub_api.patient.dto.PatientResponse;
import com.buziahub.buziahub_api.patient.dto.PatientSearchCriteria;
import com.buziahub.buziahub_api.patient.dto.PatientSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

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

}
