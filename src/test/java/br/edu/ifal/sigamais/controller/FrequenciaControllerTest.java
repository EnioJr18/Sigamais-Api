package br.edu.ifal.sigamais.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ifal.sigamais.dto.FrequenciaRequestDTO;
import br.edu.ifal.sigamais.dto.FrequenciaResponseDTO;
import br.edu.ifal.sigamais.service.FrequenciaService;

@WebMvcTest(FrequenciaController.class)
public class FrequenciaControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FrequenciaService frequenciaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar HTTP 201 (Created) ao registrar uma nova falta com sucesso")
    void deveRetornar201AoRegistrarFalta() throws Exception {
        FrequenciaRequestDTO request = new FrequenciaRequestDTO(1, 2);
        FrequenciaResponseDTO response = new FrequenciaResponseDTO(50L, 1, 2);

        when(frequenciaService.registrarFrequencia(any(FrequenciaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/frequencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(50L))
                    .andExpect(jsonPath("$.matriculaId").value(1))
                    .andExpect(jsonPath("$.faltas").value(2));
    }
}