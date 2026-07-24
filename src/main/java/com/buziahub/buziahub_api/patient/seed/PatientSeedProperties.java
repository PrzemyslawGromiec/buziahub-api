package com.buziahub.buziahub_api.patient.seed;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "seed")
public record PatientSeedProperties(
        int count,
        long randomSeed,
        boolean clearExisting
) {
}
