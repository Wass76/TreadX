package com.TreadX.controller;

import com.TreadX.patron.controller.PatronController;
import com.TreadX.patron.request.PatronRequest;
import com.TreadX.patron.response.PatronInfoResponse;
import com.TreadX.patron.response.PatronInfoResponseWrapper;
import com.TreadX.patron.service.PatronService;
import com.TreadX.utils.request.PaginationRequest;
import com.TreadX.utils.response.PaginationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PatronControllerTest {

    @Mock
    private PatronService patronService;

    @InjectMocks
    private PatronController patronController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(patronController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllPatrons_ShouldReturnPaginatedList() throws Exception {
        List<PatronInfoResponse> patrons = List.of(
                PatronInfoResponse.builder()
                        .id(UUID.randomUUID())
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@example.com")
                        .phoneNumber("+1234567890")
                        .address("123 Main St")
                        .membershipDate(LocalDate.parse("2023-10-15"))
                        .build()
        );

        PaginationResponse paginationResponse = PaginationResponse.builder()
                .page(1)
                .perPage(patrons.size())
                .total(50)
                .build();

        PatronInfoResponseWrapper responseWrapper = PatronInfoResponseWrapper.builder()
                .patronsInfo(patrons)
                .pagination(paginationResponse)
                .build();

        when(patronService.findAllPatrons(any(PaginationRequest.class))).thenReturn(responseWrapper);

        mockMvc.perform(get("/api/patrons?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(patronService).findAllPatrons(any(PaginationRequest.class));
    }

    @Test
    void getPatronById_ShouldReturnPatron() throws Exception {
        UUID patronId = UUID.randomUUID();
        PatronInfoResponse patron = PatronInfoResponse.builder()
                .id(patronId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1234567890")
                .address("123 Main St")
                .membershipDate(LocalDate.parse("2023-10-15"))
                .build();

        when(patronService.findPatronById(patronId)).thenReturn(patron);

        mockMvc.perform(get("/api/patrons/{id}", patronId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(patronService).findPatronById(patronId);
    }

    @Test
    void addPatron_ShouldCreatePatron() throws Exception {
        PatronRequest request = PatronRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1234567890")
                .address("123 Main St")
                .membershipDate(LocalDate.parse("2023-10-15"))
                .build();

        PatronInfoResponse createdPatron = PatronInfoResponse.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1234567890")
                .address("123 Main St")
                .membershipDate(LocalDate.parse("2023-10-15"))
                .build();

        when(patronService.addPatron(any(PatronRequest.class))).thenReturn(createdPatron);

        mockMvc.perform(post("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(patronService).addPatron(any(PatronRequest.class));
    }

    @Test
    void updatePatron_ShouldUpdatePatron() throws Exception {
        UUID patronId = UUID.randomUUID();
        PatronRequest request = PatronRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+1234567890")
                .address("456 Elm St")
                .membershipDate(LocalDate.parse("2023-10-15"))
                .build();

        PatronInfoResponse updatedPatron = PatronInfoResponse.builder()
                .id(patronId)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+1234567890")
                .address("456 Elm St")
                .membershipDate(LocalDate.parse("2023-10-15"))
                .build();

        when(patronService.updatePatron(any(PatronRequest.class), eq(patronId))).thenReturn(updatedPatron);

        mockMvc.perform(put("/api/patrons/{id}", patronId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(patronService).updatePatron(any(PatronRequest.class), eq(patronId));
    }

    @Test
    void deletePatron_ShouldDeletePatron() throws Exception {
        UUID patronId = UUID.randomUUID();

        when(patronService.deletePatron(patronId)).thenReturn(true);

        mockMvc.perform(delete("/api/patrons/{id}", patronId))
                .andExpect(status().isOk());

        verify(patronService).deletePatron(patronId);
    }
}
