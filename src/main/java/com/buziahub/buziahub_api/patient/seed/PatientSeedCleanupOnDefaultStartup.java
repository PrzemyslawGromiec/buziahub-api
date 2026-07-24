package com.buziahub.buziahub_api.patient.seed;

import com.buziahub.buziahub_api.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("default")
@RequiredArgsConstructor
public class PatientSeedCleanupOnDefaultStartup implements ApplicationRunner {

    private final PatientRepository patientRepository;

    @Value("${app.seed.cleanup-on-default-startup:false}")
    private boolean cleanupOnDefaultStartup;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!cleanupOnDefaultStartup) {
            return;
        }

        long deleted = patientRepository.deleteByCommentsStartingWith(PatientSeedConstants.MARKER);
        log.info("Default profile startup cleanup removed {} seeded patients", deleted);
    }
}
