package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.common.Gender;
import com.buziahub.buziahub_api.exceptions.InvalidPatientSearchCriteriaException;
import com.buziahub.buziahub_api.exceptions.PatientNotFoundException;
import com.buziahub.buziahub_api.patient.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
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
    void createPatient_shouldThrowWhenSaveFails() {

        // arrange
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
                .thenThrow(new RuntimeException("Database error"));

        // act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> patientService.createPatient(request));

        // assert
        assertEquals("Database error", exception.getMessage());
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

    @Test
    void searchPatients_shouldReturnMappedPageWhenCriteriaAreValid() {
        // happy path, dto mapping, page metadata

        //arrange
        PatientSearchCriteria criteria = new PatientSearchCriteria(
                "An",
                null,
                Gender.FEMALE,
                true,
                TextMatchMode.PREFIX
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by("lastName").ascending());

        Patient first = Patient.create(
                "Anna",
                "Smith",
                null,
                Gender.FEMALE,
                "123 Main St",
                "+447744567636",
                null,
                null
        );

        Patient second = Patient.create(
                "Ann",
                "Boolean",
                null,
                Gender.FEMALE,
                "123 Main St",
                "+447744567637",
                null,
                null
        );


        Page<Patient> repositoryPage = new PageImpl<>(List.of(first, second), pageable, 2);

        when(patientRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(repositoryPage);

        // act
        Page<PatientSummaryResponse> result = patientService.searchPatients(criteria, pageable);

        // assert
        verify(patientRepository).findAll(any(Specification.class), eq(pageable));
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(2, result.getContent().size());

        assertEquals("Anna", result.getContent().get(0).firstName());
        assertEquals("Smith", result.getContent().get(0).lastName());
        assertEquals("+447744567636", result.getContent().get(0).phoneNumber());

        assertEquals("Ann", result.getContent().get(1).firstName());
        assertEquals("Boolean", result.getContent().get(1).lastName());
        assertEquals("+447744567637", result.getContent().get(1).phoneNumber());
    }

    @Test
    void searchPatients_shouldThrowWhenContainsSearchTermIsTooShort() {
        // arrange
        // input objects, mock dependency return value, mock behaviour, shared constants, test data
        // matchMode=Contains, firstName or lastName ="B", pageable can be any real valid, repo should not be called
        // exception should be thrown
        PatientSearchCriteria criteria = new PatientSearchCriteria(
                "A",
                null,
                null,
                null,
                TextMatchMode.CONTAINS
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by("lastName").ascending());

        // act
        // Act + Assert: call the method inside assertThrows
        // because this scenario expects an exceptionrows()

        InvalidPatientSearchCriteriaException ex = assertThrows(
                InvalidPatientSearchCriteriaException.class,
                () -> patientService.searchPatients(criteria, pageable)
        );

        // assert
        verifyNoInteractions(patientRepository);
        assertEquals(
                "CONTAINS mode requires at least 2 characters for firstName and lastName",
                ex.getMessage()
        );
    }

    @Test
    void updateContactDetails_shouldUpdateContactFieldsAndPreserveOtherPatientData() {
        //arrange
        Long patientId = 1L;
        UpdatePatientContactDetails details = new UpdatePatientContactDetails(
                "456 New St, London",
                "+447755566677",
                "John Doe"
        );

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

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(patient));
        //act
        PatientResponse response = patientService.updateContactDetails(patientId, details);

        //assert
        verify(patientRepository).findById(patientId);
        verify(patientRepository, never()).save(any(Patient.class));
        assertEquals("456 New St, London", response.address());
        assertEquals("+447755566677", response.phoneNumber());
        assertEquals("John Doe", response.emergencyContact());
        assertEquals(patient.getActive(), response.active());
        assertEquals("Adam", response.firstName());
        assertEquals("Borom", response.lastName());
        assertFalse(response.emergencyContact().isBlank());
    }
}
