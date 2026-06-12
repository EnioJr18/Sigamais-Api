package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.DisciplinaRequestDTO;
import br.edu.ifal.sigamais.dto.DisciplinaResponseDTO;
import br.edu.ifal.sigamais.model.Disciplina;
import br.edu.ifal.sigamais.repository.DisciplinaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DisciplinaServiceTest {

    @InjectMocks
    private DisciplinaService disciplinaService;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Test
    void deveSalvarDisciplinaComSucesso() {
        DisciplinaRequestDTO requestDTO = new DisciplinaRequestDTO("Banco de Dados", 60);
        Disciplina disciplinaSalva = new Disciplina();
        disciplinaSalva.setId(1);
        disciplinaSalva.setNome("Banco de Dados");
        disciplinaSalva.setCargaHoraria(60);

        Mockito.when(disciplinaRepository.save(any(Disciplina.class))).thenReturn(disciplinaSalva);

        DisciplinaResponseDTO responseDTO = disciplinaService.salvar(requestDTO);

        Assertions.assertEquals(1, responseDTO.id());
        Assertions.assertEquals("Banco de Dados", responseDTO.nome());
    }
}