package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Disciplina;
import br.edu.ifal.sigamais.model.Professor;
import br.edu.ifal.sigamais.model.Turma;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

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

        Mockito.when(profRepo.findById(1)).thenReturn(Optional.of(professor));
        Mockito.when(discRepo.findById(2)).thenReturn(Optional.of(disciplina));
        Mockito.when(turmaRepo.save(any(Turma.class))).thenReturn(turmaSalva);

        Turma resultado = turmaService.salvar(1, 2, "2024.1", 2024);

        assertNotNull(resultado);
        assertEquals(10, resultado.getId());
        assertEquals("2024.1", resultado.getSemestre());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar Turma com Professor inexistente")
    void deveLancarExcecaoProfessorInexistente() {
        Mockito.when(profRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            turmaService.salvar(99, 2, "2024.1", 2024);
        });

        Mockito.verify(turmaRepo, Mockito.never()).save(any(Turma.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar Turma com Disciplina inexistente")
    void deveLancarExcecaoDisciplinaInexistente() {
        Professor professor = new Professor();
        professor.setId(1);

        Mockito.when(profRepo.findById(1)).thenReturn(Optional.of(professor));
        Mockito.when(discRepo.findById(88)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            turmaService.salvar(1, 88, "2024.1", 2024);
        });
    }
}