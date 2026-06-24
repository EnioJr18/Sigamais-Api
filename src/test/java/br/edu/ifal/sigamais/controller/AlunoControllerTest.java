package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.AlunoRequestDTO;
import br.edu.ifal.sigamais.dto.AlunoResponseDTO;
import br.edu.ifal.sigamais.model.Aluno;
import br.edu.ifal.sigamais.repository.AlunoRepository;
import br.edu.ifal.sigamais.service.AlunoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AlunoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlunoService alunoService;

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private AlunoController alunoController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Inicializa o MockMvc de forma isolada para testar apenas este Controller
        mockMvc = MockMvcBuilders.standaloneSetup(alunoController).build();
    }

    @Test
    @DisplayName("GET /alunos - Deve retornar status 200 OK e a lista de alunos")
    void deveListarAlunosComSucesso() throws Exception {
        // Arrange
        Aluno alunoMock = new Aluno();
        alunoMock.setId(1);
        alunoMock.setMatricula("2026001");
        // O seu controller acessa o repositório diretamente nesta rota, então mockamos ele:
        when(alunoRepository.findAll()).thenReturn(List.of(alunoMock));

        // Act & Assert
        mockMvc.perform(get("/alunos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].matricula").value("2026001"));

        verify(alunoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("POST /alunos - Deve retornar status 200 OK e o DTO de resposta ao cadastrar")
    void deveCadastrarAlunoComSucesso() throws Exception {
        // Arrange
        AlunoRequestDTO requestDTO = new AlunoRequestDTO("Enio Jr", "12345678996", "eniojr@gmail.com", "eniojr123", "2026001", "Sistemas de Informação", BigDecimal.valueOf(3500), 2024);
        AlunoResponseDTO responseDTO = new AlunoResponseDTO(1, "Enio Jr", "2026001", "Sistemas de Informação", "Ativo");

        // O seu controller acessa o Service nesta rota:
        when(alunoService.salvar(any(AlunoRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk()) // Como você não usou ResponseEntity.created(), o Spring retorna 200 OK por padrão
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.matricula").value("2026001"))
                .andExpect(jsonPath("$.nome").value("Enio Jr"));

        verify(alunoService, times(1)).salvar(any(AlunoRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /alunos/{id} - Deve retornar status 204 No Content ao deletar")
    void deveDeletarAlunoComSucesso() throws Exception {
        // Arrange
        // Para rotas void, não precisamos do 'when', apenas verificamos se chamou.
        Integer idParaDeletar = 1;

        // Act & Assert
        mockMvc.perform(delete("/alunos/{id}", idParaDeletar))
                .andExpect(status().isNoContent());

        // Verifica se o repositório foi acionado para excluir
        verify(alunoRepository, times(1)).deleteById(idParaDeletar);
    }
}