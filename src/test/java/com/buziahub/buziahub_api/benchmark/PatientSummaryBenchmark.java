package com.buziahub.buziahub_api.benchmark;

import com.buziahub.buziahub_api.BuziahubApiApplication;
import com.buziahub.buziahub_api.patient.PatientRepository;
import com.buziahub.buziahub_api.patient.PatientService;
import com.buziahub.buziahub_api.patient.dto.PatientSummaryResponse;
import org.openjdk.jmh.annotations.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class PatientSummaryBenchmark {

    private PatientService patientService;
    private PatientRepository patientRepository;
    private ConfigurableApplicationContext context;

    @Setup(Level.Trial)
    public void setup() {
        context = SpringApplication.run(BuziahubApiApplication.class);
        patientService = context.getBean(PatientService.class);
        patientRepository = context.getBean(PatientRepository.class);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        context.close();
    }

    @Benchmark
    public List<PatientSummaryResponse> benchmarkCurrentApproach() {
        return patientService.getAllPatientsSummary();  // Current: findAll() + stream
    }

    @Benchmark
    public List<PatientSummaryResponse> benchmarkOptimizedApproach() {
        return patientRepository.findAllSummaries();  // New: JPQL projection
    }
}
