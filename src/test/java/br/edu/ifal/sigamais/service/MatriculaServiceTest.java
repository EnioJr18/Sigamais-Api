package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.MatriculaRequestDTO;
import br.edu.ifal.sigamais.dto.MatriculaResponseDTO;
import br.edu.ifal.sigamais.exception.LimitesVagasException;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Aluno;
import br.edu.ifal.sigamais.model.Disciplina;
import br.edu.ifal.sigamais.model.Matricula;
import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.repository.AlunoRepository;
import br.edu.ifal.sigamais.repository.MatriculaRepository;
import br.edu.ifal.sigamais.repository.TurmaRepository;
import org.junit.jupiter.api.BeforeEach;
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
class MatriculaServiceTest {

    @InjectMocks
    private MatriculaService matriculaService;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private TurmaRepository turmaRepository;

    private Aluno aluno;
    private Turma turma;
    private MatriculaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1);
        aluno.setMatricula("2024001");

        Disciplina disciplina = new Disciplina();
        disciplina.setNome("Estrutura de Dados");

        turma = new Turma();
        turma.setId(2);
        turma.setVagas(30);
        turma.setDisciplina(disciplina);

        requestDTO = new MatriculaRequestDTO(1, 2);
    }

    @Test
    @DisplayName("Deve realizar matrícula com sucesso quando houver vagas disponíveis")
    void deveRealizarMatriculaComSucesso() {
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(turmaRepository.findById(2)).thenReturn(Optional.of(turma));

        Matricula matriculaSalva = new Matricula();
        matriculaSalva.setId(1);
        matriculaSalva.setAluno(aluno);
        matriculaSalva.setTurma(turma);
        matriculaSalva.setStatus("ATIVA");

        Mockito.when(matriculaRepository.save(any(Matricula.class))).thenReturn(matriculaSalva);

        MatriculaResponseDTO response = matriculaService.realizarMatricula(requestDTO);

        assertNotNull(response);
        assertEquals("ATIVA", response.status());
        assertEquals("Estrutura de Dados", response.nomeDisciplina());
        assertEquals(29, turma.getVagas());
    }

    @Test
    @DisplayName("Deve lançar LimitesVagasException quando a turma estiver lotada")
    void deveLancarExcecaoQuandoTurmaLotada() {
        turma.setVagas(0);

        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(turmaRepository.findById(2)).thenReturn(Optional.of(turma));

        // Verifica se o sistema barra a matrícula e lança a exceção correta
        assertThrows(LimitesVagasException.class, () -> {
            matriculaService.realizarMatricula(requestDTO);
        });
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException se o Aluno não existir")
    void deveLancarExcecaoQuandoAlunoNaoExistir() {
        // Simula o banco não encontrando o aluno
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            matriculaService.realizarMatricula(requestDTO);
        });
    }
}