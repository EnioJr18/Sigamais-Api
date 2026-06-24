package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.ProfessorRequestDTO;
import br.edu.ifal.sigamais.dto.ProfessorResponseDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @InjectMocks
    private ProfessorService professorService;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve cadastrar um Professor e criar seu Usuário simultaneamente")
    void deveSalvarProfessorComSucesso() {
        ProfessorRequestDTO requestDTO = new ProfessorRequestDTO(
                "Alan Turing", "turing@ifal.edu.br", "123.456.789-00", "senha123", "Doutor em Computação"
        );

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1);
        usuarioSalvo.setNome("Alan Turing");
        usuarioSalvo.setEmail("turing@ifal.edu.br");
        usuarioSalvo.setCpf("123.456.789-00");
        usuarioSalvo.setSenha("encoded_password");
        usuarioSalvo.setPerfil("PROFESSOR");

        Professor professorSalvo = new Professor();
        professorSalvo.setId(1);
        professorSalvo.setUsuario(usuarioSalvo);
        professorSalvo.setTitulacao("Doutor em Computação");

        Mockito.when(passwordEncoder.encode("senha123")).thenReturn("encoded_password");
        Mockito.when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);
        Mockito.when(professorRepository.save(any(Professor.class))).thenReturn(professorSalvo);

        ProfessorResponseDTO response = professorService.salvar(requestDTO);

        assertNotNull(response);
        assertEquals(1, response.id());
        assertEquals("Alan Turing", response.nome());
        assertEquals("turing@ifal.edu.br", response.email());
        assertEquals("Doutor em Computação", response.titulacao());

        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(professorRepository).save(any(Professor.class));
    }

    @Test
    @DisplayName("Deve listar todos os professores com sucesso")
    void deveListarTodosProfessoresComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setNome("Alan Turing");
        usuario.setEmail("turing@ifal.edu.br");

        Professor professor = new Professor();
        professor.setId(1);
        professor.setUsuario(usuario);
        professor.setTitulacao("Doutor em Computação");

        Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));

        List<ProfessorResponseDTO> response = professorService.listarTodos();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1, response.get(0).id());
        assertEquals("Alan Turing", response.get(0).nome());
        assertEquals("turing@ifal.edu.br", response.get(0).email());
        assertEquals("Doutor em Computação", response.get(0).titulacao());
    }

    @Test
    @DisplayName("Deve propagar erro do repositório em falha no banco de dados durante salvamento")
    void devePropagarExcecaoDeBancoAoSalvar() {
        ProfessorRequestDTO requestDTO = new ProfessorRequestDTO(
                "Alan Turing", "turing@ifal.edu.br", "123.456.789-00", "senha123", "Doutor em Computação"
        );

        Mockito.when(passwordEncoder.encode("senha123")).thenReturn("encoded_password");
        Mockito.when(usuarioRepository.save(any(Usuario.class))).thenThrow(new RuntimeException("Banco de dados indisponível"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            professorService.salvar(requestDTO);
        });

        assertEquals("Banco de dados indisponível", exception.getMessage());
        Mockito.verify(professorRepository, Mockito.never()).save(any(Professor.class));
    }
}