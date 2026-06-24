package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.TurmaRequestDTO;
import br.edu.ifal.sigamais.dto.TurmaResponseDTO;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Disciplina;
import br.edu.ifal.sigamais.model.Professor;
import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.repository.DisciplinaRepository;
import br.edu.ifal.sigamais.repository.ProfessorRepository;
import br.edu.ifal.sigamais.repository.TurmaRepository;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TurmaServiceTest {

    @InjectMocks
    private TurmaService turmaService;

    @Mock
    private TurmaRepository turmaRepo;

    @Mock
    private ProfessorRepository profRepo;

    @Mock
    private DisciplinaRepository discRepo;

    @Test
    @DisplayName("Deve salvar Turma com sucesso quando Professor e Disciplina existirem")
    void deveSalvarTurmaComSucesso() {
        TurmaRequestDTO requestDTO = new TurmaRequestDTO(1, 2, "2024.1", 2024, 30);

        Professor professor = new Professor();
        professor.setId(1);

        Disciplina disciplina = new Disciplina();
        disciplina.setId(2);

        Turma turmaSalva = new Turma();
        turmaSalva.setId(10);
        turmaSalva.setProfessor(professor);
        turmaSalva.setDisciplina(disciplina);
        turmaSalva.setSemestre("2024.1");
        turmaSalva.setAno(2024);
        turmaSalva.setVagas(30);

        Mockito.when(profRepo.findById(1)).thenReturn(Optional.of(professor));
        Mockito.when(discRepo.findById(2)).thenReturn(Optional.of(disciplina));
        Mockito.when(turmaRepo.save(any(Turma.class))).thenReturn(turmaSalva);

        Turma resultado = turmaService.salvar(requestDTO);

        assertNotNull(resultado);
        assertEquals(10, resultado.getId());
        assertEquals("2024.1", resultado.getSemestre());
        assertEquals(30, resultado.getVagas());
        verify(turmaRepo).save(any(Turma.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar Turma com Professor inexistente")
    void deveLancarExcecaoProfessorInexistente() {
        TurmaRequestDTO requestDTO = new TurmaRequestDTO(99, 2, "2024.1", 2024, 30);

        Mockito.when(profRepo.findById(99)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            turmaService.salvar(requestDTO);
        });

        assertEquals("Professor não encontrado com o ID: 99", exception.getMessage());
        Mockito.verify(turmaRepo, Mockito.never()).save(any(Turma.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar Turma com Disciplina inexistente")
    void deveLancarExcecaoDisciplinaInexistente() {
        TurmaRequestDTO requestDTO = new TurmaRequestDTO(1, 88, "2024.1", 2024, 30);

        Professor professor = new Professor();
        professor.setId(1);

        Mockito.when(profRepo.findById(1)).thenReturn(Optional.of(professor));
        Mockito.when(discRepo.findById(88)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            turmaService.salvar(requestDTO);
        });

        assertEquals("Disciplina não encontrada com o ID: 88", exception.getMessage());
        Mockito.verify(turmaRepo, Mockito.never()).save(any(Turma.class));
    }

    @Test
    @DisplayName("Deve listar todas as turmas com sucesso convertendo para DTO")
    void deveListarTodasTurmasComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setNome("Alan Turing");

        Professor professor = new Professor();
        professor.setId(1);
        professor.setUsuario(usuario);

        Disciplina disciplina = new Disciplina();
        disciplina.setId(2);
        disciplina.setNome("Inteligência Artificial");

        Turma turma = new Turma();
        turma.setId(10);
        turma.setSemestre("2024.1");
        turma.setAno(2024);
        turma.setVagas(40);
        turma.setProfessor(professor);
        turma.setDisciplina(disciplina);

        Mockito.when(turmaRepo.findAll()).thenReturn(List.of(turma));

        List<TurmaResponseDTO> response = turmaService.listarTodas();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(10, response.get(0).id());
        assertEquals("2024.1", response.get(0).semestre());
        assertEquals(2024, response.get(0).ano());
        assertEquals(40, response.get(0).vagas());
        assertEquals(1, response.get(0).professorId());
        assertEquals("Alan Turing", response.get(0).professorNome());
        assertEquals(2, response.get(0).disciplinaId());
        assertEquals("Inteligência Artificial", response.get(0).disciplinaNome());
    }
}