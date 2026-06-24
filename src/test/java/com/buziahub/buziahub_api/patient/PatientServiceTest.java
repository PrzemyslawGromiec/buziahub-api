package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.exceptions.PatientNotFoundException;
import com.buziahub.buziahub_api.patient.dto.CreatePatientRequest;
import com.buziahub.buziahub_api.patient.dto.PatientResponse;
import com.buziahub.buziahub_api.patient.dto.UpdatePatientNameRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void getPatientById_shouldReturnPatient() {

        Patient patient = Patient.create(
                "Adam",
                "Borom"
                , null,
                null,
                null,
                null,
                null,
                null
        );

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        PatientResponse result = patientService.getPatientById(1L);

        assertEquals("Adam", result.firstName());
        assertEquals("Borom", result.lastName());
    }

    @Test
    void getPatientById_shouldThrowWhenPatientNotFound() {

        when(patientRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.getPatientById(1L)
        );
    }

    @Test
    void repositorySave_shouldSavePatient() {

        CreatePatientRequest request =
                new CreatePatientRequest(
                        "Adam",
                        "Borom",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PatientResponse response =
                patientService.createPatient(request);

        assertEquals("Adam", response.firstName());
        assertEquals("Borom", response.lastName());

        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void archivePatient_shouldSetPatientAsInactive() {

        Patient patient = Patient.create(
                "Adam",
                "Borom"
                , null,
                null,
                null,
                null,
                null,
                null
        );

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        PatientResponse response = patientService.archivePatient(1L);

        assertFalse(response.active());
        assertFalse(patient.getActive());

        verify(patientRepository).findById(1L);
    }

    @Test
    void createPatient_shouldSaveAndReturnPatient() {

        CreatePatientRequest request = new CreatePatientRequest(
                "Adam",
                "Borom"
                , null,
                null,
                null,
                null,
                null,
                null
        );

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PatientResponse response = patientService.createPatient(request);

        assertEquals("Adam", response.firstName());
        assertEquals("Borom", response.lastName());
        assertTrue(response.active());

        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void updatePatientName_shouldUpdateOnlyProvidedFields() {

        Patient patient = Patient.create(
                "Adam",
                "Borom"
                , null,
                null,
                "London",
                "+447756467364",
                null,
                null
        );

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        UpdatePatientNameRequest request = new UpdatePatientNameRequest(
                "Stephen",
                "Mockito"
        );

        PatientResponse response = patientService.updatePatientName(
                1L,
                request
                );

        verify(patientRepository).findById(1L);

        verify(patientRepository, never()).save(any(Patient.class));

        assertEquals("Stephen", response.firstName());
        assertEquals("Mockito", response.lastName());
        assertEquals("London", response.address());
        assertEquals("+447756467364", response.phoneNumber());
        assertEquals(patient.getActive(), response.active());
    }
}
