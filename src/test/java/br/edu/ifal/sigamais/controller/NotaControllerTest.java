package br.edu.ifal.sigamais.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ifal.sigamais.dto.NotaRequestDTO;
import br.edu.ifal.sigamais.dto.NotaResponseDTO;
import br.edu.ifal.sigamais.service.NotaService;

@WebMvcTest(NotaController.class)
public class NotaControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotaService notaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar HTTP 201 (Created) ao cadastrar uma nova nota com sucesso")
    void deveRetornar201AoCadastrarNota() throws Exception {
        NotaRequestDTO request = new NotaRequestDTO(1, BigDecimal.valueOf(8.5), "AV1");
        NotaResponseDTO response = new NotaResponseDTO(100, 1, BigDecimal.valueOf(8.5), "AV1");

        when(notaService.cadastrarNota(any(NotaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/notas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.valor").value(8.5))
                .andExpect(jsonPath("$.tipo").value("AV1"));
    }
}