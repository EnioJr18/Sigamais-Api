package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.ProfessorRequestDTO;
import br.edu.ifal.sigamais.dto.ProfessorResponseDTO;
import br.edu.ifal.sigamais.model.Professor;
import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.repository.ProfessorRepository;
import br.edu.ifal.sigamais.repository.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @InjectMocks
    private ProfessorService professorService;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void deveSalvarProfessorComSucesso() {
        ProfessorRequestDTO requestDTO = new ProfessorRequestDTO(1, "Doutor");

        Usuario usuario = new Usuario();
        usuario.setId(1);

        Professor professorSalvo = new Professor();
        professorSalvo.setId(1);
        professorSalvo.setUsuario(usuario);
        professorSalvo.setTitulacao("Doutor");

        Mockito.when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuario));
        Mockito.when(professorRepository.save(any(Professor.class))).thenReturn(professorSalvo);

        ProfessorResponseDTO responseDTO = professorService.salvar(requestDTO);

        Assertions.assertEquals(1, responseDTO.id());
        Assertions.assertEquals("Doutor", responseDTO.titulacao());
    }
}