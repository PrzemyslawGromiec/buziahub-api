package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.patient.dto.PatientNameResponse;
import com.buziahub.buziahub_api.patient.dto.PatientSummaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {

    List<Patient> findByActiveTrue();

    List<Patient> findByFirstNameStartingWith(String prefix);

    @Query("""
                SELECT new com.buziahub.buziahub_api.patient.dto.PatientSummaryResponse(
                    p.firstName,
                    p.lastName,
                    p.phoneNumber
                )
                FROM Patient p
                WHERE p.active = true
            """)
    List<PatientSummaryResponse> findAllSummaries();

    @Query("""
                SELECT new com.buziahub.buziahub_api.patient.dto.PatientNameResponse(
                    p.id,
                    CONCAT(p.firstName, ' ', p.lastName)
                )
                FROM Patient p
                WHERE p.active = true
            """)
    List<PatientNameResponse> findActivePatientNames();

    long countByCommentsStartingWith(String prefix);
    long deleteByCommentsStartingWith(String prefix);
}