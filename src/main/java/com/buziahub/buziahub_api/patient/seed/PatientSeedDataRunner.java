package com.buziahub.buziahub_api.patient.seed;

import com.buziahub.buziahub_api.common.Gender;
import com.buziahub.buziahub_api.patient.Patient;
import com.buziahub.buziahub_api.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@Profile("seed")
@RequiredArgsConstructor
public class PatientSeedDataRunner implements CommandLineRunner {

    private static final List<String> FIRST_NAMES = List.of(
            "John", "Jane", "Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Hank",
            "Ivy", "Jack", "Kathy", "Leo", "Mona", "Nate", "Olivia", "Paul", "Quinn", "Rachel", "Steve",
            "Tina", "Uma", "Victor", "Wendy", "Xander", "Yara", "Zane", "Oliver", "Sophia", "Liam", "Emma", "Noah",
            "Ava", "Elijah", "Isabella", "James", "Mia");

    private static final List<String> LAST_NAMES = List.of(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez",
            "Wilson", "Martinez", "Anderson", "Taylor", "Thomas", "Hernandez", "Moore", "Martin", "Jackson",
            "Thompson", "White", "Lopez", "Lee", "Gonzalez", "Harris", "Clark", "Lewis", "Robinson", "Walker",
            "Perez", "Hall", "Young", "Allen", "Sanchez");

    private static final Gender[] GENDERS = Gender.values();

    private final PatientRepository patientRepository;
    private final PatientSeedProperties seedProperties;

    @Override
    @Transactional
    public void run(String... args) {

        log.info("Starting patient seeding...");
        if (seedProperties.clearExisting()) {
            long deleted = patientRepository.deleteByCommentsStartingWith(
                    PatientSeedConstants.MARKER
            );
            log.info("Removed {} previously seeded patients.", deleted);
        }

        int requestedCount = seedProperties.count();
        long existingSeeded = patientRepository.countByCommentsStartingWith(PatientSeedConstants.MARKER);

        if (existingSeeded > 0) {
            log.info(
                    "Skipping patient seeding because {} seeded patients already exist.",
                    existingSeeded
            );
            return;
        }

        log.info(
                "Generating {} seeded patients using random seed {}.",
                requestedCount,
                seedProperties.randomSeed()
        );

        Random random = new Random(seedProperties.randomSeed());

        List<Patient> patients = new ArrayList<>(requestedCount);
        for (int i = 0; i < requestedCount; i++) {
            patients.add(createPatient(i, random));
        }

        patientRepository.saveAll(patients);

        log.info(
                "Patient seeding completed successfully. {} patients were created.",
                patients.size()
        );
    }

    private Patient createPatient(int i, Random random) {
        return Patient.create(
                getRandomFirstName(random),
                getRandomLastName(random),
                getRandomBirthDate(random),
                getRandomGender(random),
                "Seed street " + (i + 1),
                getRandomPhoneNumber(i),
                "Emergency Contact " + (i + 1),
                PatientSeedConstants.MARKER + " Seeded patient " + (i + 1)
        );
    }

    private String getRandomFirstName(Random random) {
        return FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
    }

    private String getRandomLastName(Random random) {
        return LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
    }

    private LocalDate getRandomBirthDate(Random random) {
        int year = 1950 + random.nextInt(70); // Random year between 1950 and 2019
        int month = 1 + random.nextInt(12); // Random month between 1 and 12
        int day = 1 + random.nextInt(28); // Random day between 1 and 28 to avoid invalid dates
        return LocalDate.of(year, month, day);
    }

    private Gender getRandomGender(Random random) {
        return GENDERS[random.nextInt(GENDERS.length)];
    }

    private String getRandomPhoneNumber(int i) {
        return String.format("+447%09d", i);
    }
}
