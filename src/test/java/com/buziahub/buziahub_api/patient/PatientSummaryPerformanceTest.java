package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.common.Gender;
import com.buziahub.buziahub_api.patient.dto.PatientSummaryResponse;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PatientSummaryPerformanceTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SessionFactory sessionFactory;

    @BeforeAll
    @Transactional
    void createTestData() {
        long currentCount = patientRepository.count();
        if (currentCount < 100) {
            System.out.println("\n=== Creating test data ===");
            createTestPatients(10000);
            System.out.println("Created " + patientRepository.count() + " patients\n");
        } else {
            System.out.println("\n=== Using existing " + currentCount + " patients ===\n");
        }
    }

    private void createTestPatients(int count) {
        for (int i = 0; i < count; i++) {
            Patient patient = Patient.create(
                    "PerfTest_FirstName" + i,
                    "PerfTest_LastName" + i,
                    LocalDate.of(1990, 1, 1),
                    Gender.OTHER,
                    "Address " + i,
                    "555-000" + (i % 10000),
                    "Emergency " + i,
                    "Comments " + i
            );
            patientRepository.save(patient);
        }
    }

    @BeforeEach
    void setupStats() {
        // Clear stats before each test
        Statistics stats = sessionFactory.getStatistics();
        stats.clear();
        stats.setStatisticsEnabled(true);
    }

    @Test
    @Transactional(readOnly = true)
    void benchmarkCurrentApproach() {
        Statistics stats = sessionFactory.getStatistics();
        stats.clear();

        System.out.println("========== CURRENT APPROACH (findAll + stream) ==========");
        long startTime = System.nanoTime();

        List<PatientSummaryResponse> results = patientService.getAllPatientsSummary();

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        printStats(stats, duration, results.size());
        assertTrue(results.size() > 0);
    }

    @Test
    @Transactional(readOnly = true)
    void benchmarkOptimizedApproach() {
        Statistics stats = sessionFactory.getStatistics();
        stats.clear();

        System.out.println("========== OPTIMIZED APPROACH (JPQL projection) ==========");
        long startTime = System.nanoTime();

        List<PatientSummaryResponse> results = patientRepository.findAllSummaries();

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        printStats(stats, duration, results.size());
        assertTrue(results.size() > 0);
    }

    private void printStats(Statistics stats, long duration, int resultCount) {
        System.out.println("Execution Time: " + duration + " ms");
        System.out.println("Results Count: " + resultCount);
        System.out.println("Queries Executed: " + stats.getQueryExecutionCount());
        System.out.println("Entities Loaded: " + stats.getEntityLoadCount());
        System.out.println("Cache Hits: " + stats.getQueryCacheHitCount());

        System.out.println("\n--- Query Statistics ---");
        for (String query : stats.getQueries()) {
            System.out.println("\nQuery: " + query);
            var stat = stats.getQueryStatistics(query);
            System.out.println("  Execution Count: " + stat.getExecutionCount());
            System.out.println("  Avg Time: " + stat.getExecutionAvgTime() + " ms");
            System.out.println("  Rows: " + stat.getExecutionRowCount());
        }
    }
}
