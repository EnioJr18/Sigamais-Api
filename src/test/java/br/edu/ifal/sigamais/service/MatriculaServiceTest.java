package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.MatriculaRequestDTO;
import br.edu.ifal.sigamais.exception.LimitesVagasException;
import br.edu.ifal.sigamais.model.Aluno;
import br.edu.ifal.sigamais.model.Matricula;
import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.repository.AlunoRepository;
import br.edu.ifal.sigamais.repository.MatriculaRepository;
import br.edu.ifal.sigamais.repository.TurmaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private TurmaRepository turmaRepository;
    @Mock
    private MatriculaRepository matriculaRepository;

    @InjectMocks
    private MatriculaService service;

    @Test
    void deveLancarExcecaoQuandoTurmaEstiverCheia_RN04() {

        // 1. PREPARAÇÃO (Arrange)
        MatriculaRequestDTO request = new MatriculaRequestDTO(1, 1); // ID do Aluno e ID da Turma

        Aluno alunoMock = new Aluno();
        alunoMock.setId(1);

        Turma turmaMock = new Turma();
        turmaMock.setId(1);
        turmaMock.setVagas(30); // Turma suporta 30 alunos

        // Ensinando o Mockito como o banco deve responder
        Mockito.when(alunoRepository.findById(1)).thenReturn(Optional.of(alunoMock));
        Mockito.when(turmaRepository.findById(1)).thenReturn(Optional.of(turmaMock));

        Mockito.when(matriculaRepository.countByTurmaId(1)).thenReturn(30L);

        // 2 e 3. AÇÃO e VERIFICAÇÃO (Act & Assert)
        // Pedimos para o JUnit garantir que tentar matricular vai lançar o LimitesVagasException
        LimitesVagasException erro = assertThrows(LimitesVagasException.class, () -> {
            service.realizarMatricula(request);
        });

        assertEquals("A turma selecionada não possui vagas disponíveis.", erro.getMessage());

        Mockito.verify(matriculaRepository, Mockito.never()).save(Mockito.any(Matricula.class));
    }
}