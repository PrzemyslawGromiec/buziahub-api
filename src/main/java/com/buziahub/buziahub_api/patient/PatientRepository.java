package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.common.Gender;
import com.buziahub.buziahub_api.patient.dto.PatientNameResponse;
import com.buziahub.buziahub_api.patient.dto.PatientSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query(value = """
            SELECT new com.buziahub.buziahub_api.patient.dto.PatientSummaryResponse(
                p.firstName,
                p.lastName,
                p.phoneNumber
            )
            FROM Patient p
            WHERE (:firstName IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
              AND (:lastName  IS NULL OR LOWER(p.lastName)  LIKE LOWER(CONCAT(:lastName, '%')))
              AND (:active    IS NULL OR p.active = :active)
              AND (:gender    IS NULL OR p.gender = :gender)
            """,
            countQuery = """
                    SELECT COUNT(p)
                    FROM Patient p
                    WHERE (:firstName IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT(:firstName, '%')))
                      AND (:lastName  IS NULL OR LOWER(p.lastName)  LIKE LOWER(CONCAT(:lastName, '%')))
                      AND (:active    IS NULL OR p.active = :active)
                      AND (:gender    IS NULL OR p.gender = :gender)
                    """)
    Page<PatientSummaryResponse> searchSummaries(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("active") Boolean active,
            @Param("gender") Gender gender,
            Pageable pageable
    );
}