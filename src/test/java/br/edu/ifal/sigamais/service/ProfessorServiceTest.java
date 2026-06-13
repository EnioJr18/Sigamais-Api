package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.ProfessorRequestDTO;
import br.edu.ifal.sigamais.dto.ProfessorResponseDTO;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Professor;
import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.repository.ProfessorRepository;
import br.edu.ifal.sigamais.repository.UsuarioRepository;
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
class ProfessorServiceTest {

    @InjectMocks
    private ProfessorService professorService;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve cadastrar um Professor vinculando-o a um Usuário existente")
    void deveSalvarProfessorComSucesso() {
        ProfessorRequestDTO requestDTO = new ProfessorRequestDTO(1, "Doutor em Computação");

        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNome("Alan Turing");

        Professor professorSalvo = new Professor();
        professorSalvo.setId(1);
        professorSalvo.setUsuario(usuario);
        professorSalvo.setTitulacao("Doutor em Computação");

        Mockito.when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        Mockito.when(professorRepository.save(any(Professor.class))).thenReturn(professorSalvo);

        ProfessorResponseDTO response = professorService.salvar(requestDTO);

        assertNotNull(response);
        assertEquals("Doutor em Computação", response.titulacao());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException se o ID do Usuário não existir no banco")
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        ProfessorRequestDTO requestDTO = new ProfessorRequestDTO(99, "Mestre");

        Mockito.when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            professorService.salvar(requestDTO);
        });

        Mockito.verify(professorRepository, Mockito.never()).save(any(Professor.class));
    }
}