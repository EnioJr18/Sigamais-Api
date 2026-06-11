package br.edu.ifal.sigamais.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.repository.DisciplinaRepository;
import br.edu.ifal.sigamais.repository.ProfessorRepository;
import br.edu.ifal.sigamais.repository.TurmaRepository;

@ExtendWith(MockitoExtension.class)
class TurmaServiceTest {

    @Mock private ProfessorRepository profRepo;
    @Mock private DisciplinaRepository discRepo;
    @Mock private TurmaRepository turmaRepo;

    @InjectMocks private TurmaService turmaService;

    @Test
    void deveLancarExcecaoQuandoProfessorNaoExistir() {
        // Simula que o banco não encontrou o professor de ID 99
        Mockito.when(profRepo.findById(99)).thenReturn(Optional.empty());

        // Verifica se o erro RecursoNaoEncontradoException é lançado
        assertThrows(RecursoNaoEncontradoException.class, () -> {
            turmaService.salvar(99, 1, "2026.1", 2026);
        });
    }
}