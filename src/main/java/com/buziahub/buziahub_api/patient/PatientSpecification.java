package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.patient.dto.PatientSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

public final class PatientSpecification {

    private PatientSpecification() {

    }

    public static Specification<Patient> withCriteria(PatientSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.firstName() != null &&
                    !criteria.firstName().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("firstName")),
                        "%" + criteria.firstName().toLowerCase() + "%"
                ));
            }
            if (criteria.lastName() != null &&
                    !criteria.lastName().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("lastName")),
                        "%" + criteria.lastName().toLowerCase() + "%"
                ));
            }

            if (criteria.gender() != null) {
                predicates.add(
                        cb.equal(
                                root.get("gender"),
                                criteria.gender()
                        )
                );
            }

            if (criteria.active() != null) {
                predicates.add(
                        cb.equal(
                                root.get("active"),
                                criteria.active()
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
