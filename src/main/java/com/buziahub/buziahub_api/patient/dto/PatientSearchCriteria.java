package com.buziahub.buziahub_api.patient.dto;

import com.buziahub.buziahub_api.common.Gender;

public record PatientSearchCriteria(
        String firstName,
        String lastName,
        Gender gender,
        Boolean active
) {
}
