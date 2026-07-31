package com.buziahub.buziahub_api.patient;

import com.buziahub.buziahub_api.common.Gender;
import com.buziahub.buziahub_api.exceptions.GlobalExceptionHandler;
import com.buziahub.buziahub_api.patient.dto.PatientSearchCriteria;
import com.buziahub.buziahub_api.patient.dto.PatientSummaryResponse;
import com.buziahub.buziahub_api.patient.dto.TextMatchMode;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @Test
    void shouldPassMaleGenderCriteriaToServiceWhenSearchingPatients() throws Exception {

        PatientSummaryResponse firstPatient = new PatientSummaryResponse("Adam", "Smith", "+447700900111");
        PatientSummaryResponse secondPatient = new PatientSummaryResponse("John", "Brown", "+447700900222");

        Page<PatientSummaryResponse> resultPage = new PageImpl<>(
                List.of(firstPatient, secondPatient)
        );


        given(patientService.searchPatients(
                eq(new PatientSearchCriteria(null, null, Gender.MALE, null, null)),
                any(Pageable.class)
        )).willReturn(resultPage);

        mockMvc.perform(get("/api/v1/patients/search")
                        .param("gender", "MALE")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].firstName").value("Adam"))
                .andExpect(jsonPath("$.content[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.content[0].phoneNumber").value("+447700900111"))
                .andExpect(jsonPath("$.content[1].firstName").value("John"))
                .andExpect(jsonPath("$.content[1].lastName").value("Brown"))
                .andExpect(jsonPath("$.content[1].phoneNumber").value("+447700900222"));

        ArgumentCaptor<PatientSearchCriteria> criteriaCaptor = ArgumentCaptor.forClass(PatientSearchCriteria.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(patientService).searchPatients(criteriaCaptor.capture(), pageableCaptor.capture());

        PatientSearchCriteria capturedCriteria = criteriaCaptor.getValue();
        Pageable capturedPageable = pageableCaptor.getValue();

        assertThat(capturedCriteria.gender()).isEqualTo(Gender.MALE);
        assertThat(capturedCriteria.firstName()).isNull();
        assertThat(capturedCriteria.lastName()).isNull();
        assertThat(capturedCriteria.active()).isNull();
        assertThat(capturedCriteria.matchMode()).isNull();

        assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
        assertThat(capturedPageable.getPageSize()).isEqualTo(10);
        assertThat(capturedPageable.getSort().getOrderFor("lastName")).isNotNull();
        assertThat(Objects.requireNonNull(capturedPageable.getSort().getOrderFor("lastName")).isAscending()).isTrue();
    }

    // GET /api/v1/patients/search?firstName=Ann&lastName=Sm&matchMode=PREFIX&page=0&size=5&sort=firstName,asc
    @Test
    void shouldPassMultipleSearchCriteriaToServiceWhenSearchingPatients() throws Exception {
        PatientSummaryResponse firstPatient = new PatientSummaryResponse("Anna", "Smith", "+447700900111");
        PatientSummaryResponse secondPatient = new PatientSummaryResponse("Annabel", "Smythe", "+447700900222");

        Page<PatientSummaryResponse> resultPage = new PageImpl<>(
                List.of(firstPatient, secondPatient)
        );

        given(patientService.searchPatients(
                any(PatientSearchCriteria.class),
                any(Pageable.class)
        )).willReturn(resultPage);

        mockMvc.perform(get("/api/v1/patients/search")
                        .param("firstName", "Ann")
                        .param("lastName", "Sm")
                        .param("matchMode", "PREFIX")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "firstName,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].firstName").value("Anna"))
                .andExpect(jsonPath("$.content[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.content[0].phoneNumber").value("+447700900111"))
                .andExpect(jsonPath("$.content[1].firstName").value("Annabel"))
                .andExpect(jsonPath("$.content[1].lastName").value("Smythe"))
                .andExpect(jsonPath("$.content[1].phoneNumber").value("+447700900222"));

        ArgumentCaptor<PatientSearchCriteria> criteriaCaptor = ArgumentCaptor.forClass(PatientSearchCriteria.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(patientService).searchPatients(criteriaCaptor.capture(), pageableCaptor.capture());
        PatientSearchCriteria capturedCriteria = criteriaCaptor.getValue();
        Pageable capturedPageable = pageableCaptor.getValue();

        assertThat(capturedCriteria.firstName()).isEqualTo("Ann");
        assertThat(capturedCriteria.lastName()).isEqualTo("Sm");
        assertThat(capturedCriteria.matchMode()).isEqualTo(TextMatchMode.PREFIX);
        assertThat(capturedCriteria.gender()).isNull();
        assertThat(capturedCriteria.active()).isNull();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
        assertThat(capturedPageable.getPageSize()).isEqualTo(5);
        assertThat(capturedPageable.getSort().getOrderFor("firstName")).isNotNull();
        assertThat(Objects.requireNonNull(capturedPageable.getSort().getOrderFor("firstName")).isAscending()).isTrue();
    }

    @Test
    void shouldReturnBadRequestWhenSearchingPatientsWithInvalidMatchMode() throws Exception {

        mockMvc.perform(get("/api/v1/patients/search")
                        .param("matchMode", "WRONG")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("Request body contains invalid or missing values"))
                .andExpect(jsonPath("$.fieldErrors.matchMode").exists())
                .andExpect(jsonPath("$.fieldErrors.matchMode")
                        .value(org.hamcrest.Matchers.containsString("Failed to convert")));

        verifyNoInteractions(patientService);
    }
}