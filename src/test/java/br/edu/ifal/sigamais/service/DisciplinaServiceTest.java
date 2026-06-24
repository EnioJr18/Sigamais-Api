package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.DisciplinaRequestDTO;
import br.edu.ifal.sigamais.dto.DisciplinaResponseDTO;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Disciplina;
import br.edu.ifal.sigamais.repository.DisciplinaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class DisciplinaServiceTest {

    @InjectMocks
    private DisciplinaService disciplinaService;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Test
    @DisplayName("Deve salvar uma Disciplina com sucesso")
    void deveSalvarDisciplinaComSucesso() {
        DisciplinaRequestDTO requestDTO = new DisciplinaRequestDTO("Banco de Dados", 60);

        Disciplina disciplinaSalva = new Disciplina();
        disciplinaSalva.setId(1);
        disciplinaSalva.setNome("Banco de Dados");
        disciplinaSalva.setCargaHoraria(60);

        Mockito.when(disciplinaRepository.save(any(Disciplina.class))).thenReturn(disciplinaSalva);

        DisciplinaResponseDTO response = disciplinaService.salvar(requestDTO);

        assertNotNull(response);
        assertEquals(1, response.id());
        assertEquals("Banco de Dados", response.nome());
        assertEquals(60, response.cargaHoraria());
        Mockito.verify(disciplinaRepository).save(any(Disciplina.class));
    }

    @Test
    @DisplayName("Deve listar todas as disciplinas com sucesso")
    void deveListarTodasAsDisciplinasComSucesso() {
        Disciplina disciplina1 = new Disciplina();
        disciplina1.setId(1);
        disciplina1.setNome("Banco de Dados");
        disciplina1.setCargaHoraria(60);

        Disciplina disciplina2 = new Disciplina();
        disciplina2.setId(2);
        disciplina2.setNome("Programação Orientada a Objetos");
        disciplina2.setCargaHoraria(80);

        Mockito.when(disciplinaRepository.findAll()).thenReturn(List.of(disciplina1, disciplina2));

        List<DisciplinaResponseDTO> response = disciplinaService.listarTodas();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Banco de Dados", response.get(0).nome());
        assertEquals(60, response.get(0).cargaHoraria());
        assertEquals("Programação Orientada a Objetos", response.get(1).nome());
        assertEquals(80, response.get(1).cargaHoraria());
    }

    @Test
    @DisplayName("Deve tratar erro ao tentar salvar uma disciplina com dados inválidos")
    void deveLancarExcecaoAoFalharSalvamento() {
        DisciplinaRequestDTO requestDTO = new DisciplinaRequestDTO(null, -10);

        Mockito.when(disciplinaRepository.save(any(Disciplina.class)))
               .thenThrow(new RuntimeException("Erro de integridade de dados"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            disciplinaService.salvar(requestDTO);
        });

        assertEquals("Erro de integridade de dados", exception.getMessage());
        Mockito.verify(disciplinaRepository).save(any(Disciplina.class));
    }

    @Test
    @DisplayName("Deve retornar a entidade Disciplina quando o ID existir no banco")
    void deveRetornarEntidadeQuandoIdExistir() {
        Disciplina disciplina = new Disciplina();
        disciplina.setId(1);
        disciplina.setNome("Engenharia de Software");

        Mockito.when(disciplinaRepository.findById(1)).thenReturn(Optional.of(disciplina));

        Disciplina resultado = disciplinaService.buscarEntidadePorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Engenharia de Software", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar Disciplina com ID inexistente")
    void deveLancarExcecaoQuandoDisciplinaNaoExistir() {
        Mockito.when(disciplinaRepository.findById(anyInt())).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            disciplinaService.buscarEntidadePorId(99);
        });

        assertEquals("Disciplina não encontrada.", exception.getMessage());
    }
}