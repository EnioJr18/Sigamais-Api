package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.MatriculaRequestDTO;
import br.edu.ifal.sigamais.dto.MatriculaResponseDTO;
import br.edu.ifal.sigamais.exception.LimitesVagasException;
import br.edu.ifal.sigamais.exception.PreRequisitoNaoAtendidoException;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Aluno;
import br.edu.ifal.sigamais.model.Disciplina;
import br.edu.ifal.sigamais.model.Matricula;
import br.edu.ifal.sigamais.model.Professor;
import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.model.Usuario;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

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
    private Disciplina disciplina;

    @BeforeEach
    void setUp() {
        Usuario usuarioAluno = new Usuario();
        usuarioAluno.setNome("João");

        aluno = new Aluno();
        aluno.setId(1);
        aluno.setMatricula("2024001");
        aluno.setUsuario(usuarioAluno);

        disciplina = new Disciplina();
        disciplina.setId(10);
        disciplina.setNome("Estrutura de Dados");

        Usuario usuarioProfessor = new Usuario();
        usuarioProfessor.setNome("Maria");
        
        Professor professor = new Professor();
        professor.setUsuario(usuarioProfessor);

        turma = new Turma();
        turma.setId(2);
        turma.setVagas(30);
        turma.setDisciplina(disciplina);
        turma.setProfessor(professor);
        turma.setSemestre("2024.1");
        turma.setAno(2024);

        requestDTO = new MatriculaRequestDTO(1, 2);
    }

    @Test
    @DisplayName("Deve realizar matrícula com sucesso quando houver vagas e sem pré-requisitos")
    void deveRealizarMatriculaComSucesso() {
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(turmaRepository.findById(2)).thenReturn(Optional.of(turma));
        Mockito.when(matriculaRepository.countByTurmaId(2)).thenReturn(10L); // 10 matrículas, sobra 20 vagas

        Matricula matriculaSalva = new Matricula();
        matriculaSalva.setId(1);
        matriculaSalva.setAluno(aluno);
        matriculaSalva.setTurma(turma);
        matriculaSalva.setStatus("ATIVA");

        Mockito.when(matriculaRepository.save(any(Matricula.class))).thenReturn(matriculaSalva);

        MatriculaResponseDTO response = matriculaService.realizarMatricula(requestDTO);

        assertNotNull(response);
        assertEquals(1, response.alunoId());
        assertEquals("João", response.alunoNome());
        assertEquals("2024001", response.alunoMatricula());
        assertEquals(2, response.turmaId());
        assertEquals("Estrutura de Dados", response.disciplinaNome());
        assertEquals("Maria", response.professorNome());
        assertEquals("2024.1", response.semestre());
        assertEquals(2024, response.ano());
        verify(matriculaRepository).save(any(Matricula.class));
    }

    @Test
    @DisplayName("Deve realizar matrícula com sucesso quando houver vagas e aluno possuir o pré-requisito")
    void deveRealizarMatriculaComSucessoComPreRequisito() {
        Disciplina preRequisito = new Disciplina();
        preRequisito.setId(5);
        disciplina.setPreRequisito(preRequisito);

        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(turmaRepository.findById(2)).thenReturn(Optional.of(turma));
        Mockito.when(matriculaRepository.countByTurmaId(2)).thenReturn(10L);
        Mockito.when(matriculaRepository.existsByAlunoIdAndTurmaDisciplinaIdAndStatus(1, 5, "APROVADO")).thenReturn(true);

        Matricula matriculaSalva = new Matricula();
        matriculaSalva.setId(1);
        matriculaSalva.setAluno(aluno);
        matriculaSalva.setTurma(turma);
        matriculaSalva.setStatus("ATIVA");

        Mockito.when(matriculaRepository.save(any(Matricula.class))).thenReturn(matriculaSalva);

        MatriculaResponseDTO response = matriculaService.realizarMatricula(requestDTO);

        assertNotNull(response);
        assertEquals("Estrutura de Dados", response.disciplinaNome());
    }

    @Test
    @DisplayName("Deve lançar PreRequisitoNaoAtendidoException se o aluno não tiver cursado o pré-requisito")
    void deveLancarExcecaoQuandoNaoAtenderPreRequisito() {
        Disciplina preRequisito = new Disciplina();
        preRequisito.setId(5);
        disciplina.setPreRequisito(preRequisito);

        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(turmaRepository.findById(2)).thenReturn(Optional.of(turma));
        Mockito.when(matriculaRepository.countByTurmaId(2)).thenReturn(10L);
        Mockito.when(matriculaRepository.existsByAlunoIdAndTurmaDisciplinaIdAndStatus(1, 5, "APROVADO")).thenReturn(false);

        PreRequisitoNaoAtendidoException exception = assertThrows(PreRequisitoNaoAtendidoException.class, () -> {
            matriculaService.realizarMatricula(requestDTO);
        });

        assertEquals("O aluno não possui o pré-requisito necessário para esta disciplina.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar LimitesVagasException quando a turma estiver lotada")
    void deveLancarExcecaoQuandoTurmaLotada() {
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(turmaRepository.findById(2)).thenReturn(Optional.of(turma));
        Mockito.when(matriculaRepository.countByTurmaId(2)).thenReturn(30L); // Limite da turma é 30

        LimitesVagasException exception = assertThrows(LimitesVagasException.class, () -> {
            matriculaService.realizarMatricula(requestDTO);
        });

        assertEquals("A turma selecionada não possui vagas disponíveis.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException se o Aluno não existir")
    void deveLancarExcecaoQuandoAlunoNaoExistir() {
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            matriculaService.realizarMatricula(requestDTO);
        });

        assertEquals("Aluno não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException se a Turma não existir")
    void deveLancarExcecaoQuandoTurmaNaoExistir() {
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        Mockito.when(turmaRepository.findById(2)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            matriculaService.realizarMatricula(requestDTO);
        });

        assertEquals("Turma não encontrada.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve listar todas as matrículas com sucesso")
    void deveListarTodasMatriculasComSucesso() {
        Matricula matricula = new Matricula();
        matricula.setId(1);
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setStatus("ATIVA");

        Mockito.when(matriculaRepository.findAll()).thenReturn(List.of(matricula));

        List<MatriculaResponseDTO> response = matriculaService.listarMatriculas();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("João", response.get(0).alunoNome());
        assertEquals("Estrutura de Dados", response.get(0).disciplinaNome());
        assertEquals("Maria", response.get(0).professorNome());
    }

    @Test
    @DisplayName("Deve propagar erro do repositório em falha no banco de dados")
    void devePropagarExcecaoDeBanco() {
        Mockito.when(alunoRepository.findById(anyInt())).thenThrow(new RuntimeException("Banco de dados fora do ar"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matriculaService.realizarMatricula(requestDTO);
        });

        assertEquals("Banco de dados fora do ar", exception.getMessage());
    }
}