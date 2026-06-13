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
        assertEquals("Banco de Dados", response.nome());
        assertEquals(60, response.cargaHoraria());
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
        assertEquals("Engenharia de Software", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar Disciplina com ID inexistente")
    void deveLancarExcecaoQuandoDisciplinaNaoExistir() {
        Mockito.when(disciplinaRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            disciplinaService.buscarEntidadePorId(99);
        });
    }
}