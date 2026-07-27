package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.patient.dto.PatientNameResponse;
import com.buziahub.buziahub_api.patient.dto.PatientSummaryResponse;
import com.buziahub.buziahub_api.patient.dto.RecentPatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                ORDER BY p.id ASC
            """)
    List<PatientSummaryResponse> findAllSummaries();

    @Query("""
                SELECT new com.buziahub.buziahub_api.patient.dto.PatientNameResponse(
                    p.id,
                    CONCAT(p.firstName, ' ', p.lastName)
                )
                FROM Patient p
                WHERE p.active = true
                ORDER BY p.id ASC
            """)
    List<PatientNameResponse> findActivePatientNames();

    @Query("""
            SELECT new com.buziahub.buziahub_api.patient.dto.RecentPatientResponse(
                    p.id,
                    p.firstName,
                    p.lastName,
                    p.createdAt
                )
                FROM Patient p
                WHERE p.active = true
                ORDER BY p.createdAt DESC, p.id DESC
            """)
    Page<RecentPatientResponse> findRecentActivePatients(Pageable pageable);

    long countByCommentsStartingWith(String prefix);
    long deleteByCommentsStartingWith(String prefix);
}