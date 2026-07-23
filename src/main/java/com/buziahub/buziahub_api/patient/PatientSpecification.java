package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.patient.dto.PatientSearchCriteria;
import com.buziahub.buziahub_api.patient.dto.TextMatchMode;
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
            TextMatchMode mode = criteria.matchMode() == null ? TextMatchMode.PREFIX : criteria.matchMode();

            addTextPredicate(predicates, cb, root, "firstName", criteria.firstName(), mode);
            addTextPredicate(predicates, cb, root, "lastName", criteria.lastName(), mode);

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

    private static void addTextPredicate(
            List<Predicate> predicates,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            jakarta.persistence.criteria.Root<Patient> root,
            String fieldName,
            String rawValue,
            TextMatchMode mode
    ) {
        if (rawValue != null && !rawValue.isBlank()) {
            String value = rawValue.trim().toLowerCase();
            String pattern = mode == TextMatchMode.CONTAINS ? "%" + value + "%" : value + "%";
            predicates.add(cb.like(cb.lower(root.get(fieldName)), pattern));
        }
    }
}
