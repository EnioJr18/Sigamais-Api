package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.AlertaRiscoDTO;
import br.edu.ifal.sigamais.dto.AlterarSenhaDTO;
import br.edu.ifal.sigamais.dto.UsuarioPerfilDTO;
import br.edu.ifal.sigamais.model.Aluno;
import br.edu.ifal.sigamais.model.Disciplina;
import br.edu.ifal.sigamais.model.Matricula;
import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.repository.AlunoRepository;
import br.edu.ifal.sigamais.repository.MatriculaRepository;
import br.edu.ifal.sigamais.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AnaliseRiscoService analiseRiscoService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        // 1. Simular o Usuário Autenticado no Spring Security
        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setEmail("aluno@ifal.edu.br");
        usuarioMock.setNome("Enio Jr");
        usuarioMock.setPerfil("ALUNO");
        usuarioMock.setSenha("senha_hash_banco");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("aluno@ifal.edu.br");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // 2. Comportamento padrão: Quando buscar o e-mail logado, devolve o nosso mock
        when(usuarioRepository.findByEmail("aluno@ifal.edu.br")).thenReturn(usuarioMock);
    }

    @AfterEach
    void tearDown() {
        // Limpa o contexto de segurança após cada teste para não poluir os outros
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("obterMeuPerfil - Deve retornar emRisco como TRUE se aluno tiver risco ALTO")
    void deveRetornarEmRiscoTrueParaRiscoAlto() {
        // Arrange (Prepara o terreno para ele achar uma matrícula com risco ALTO)
        Aluno alunoMock = new Aluno();
        alunoMock.setId(10);
        alunoMock.setUsuario(usuarioMock);

        when(alunoRepository.findAll()).thenReturn(List.of(alunoMock));

        Disciplina disciplina = new Disciplina();
        disciplina.setNome("Matemática");
        Turma turma = new Turma();
        turma.setDisciplina(disciplina);

        Matricula matriculaMock = new Matricula();
        matriculaMock.setId(100);
        matriculaMock.setAluno(alunoMock);
        matriculaMock.setTurma(turma);

        when(matriculaRepository.findAll()).thenReturn(List.of(matriculaMock));

        // Aqui está a mágica: O Serviço de risco avisa que é ALTO
        AlertaRiscoDTO riscoAlto = new AlertaRiscoDTO("ALTO", BigDecimal.ZERO, 15, List.of("Muitas faltas"));
        when(analiseRiscoService.analisarRiscoMatricula(100)).thenReturn(riscoAlto);

        // Act
        UsuarioPerfilDTO perfil = usuarioService.obterMeuPerfil();

        // Assert
        assertTrue(perfil.emRisco(), "A flag emRisco deveria ser TRUE porque o aluno tem risco ALTO.");
    }

    @Test
    @DisplayName("obterMeuPerfil - Deve retornar emRisco como FALSE se aluno não tiver risco ALTO")
    void deveRetornarEmRiscoFalseParaRiscoBaixo() {
        // Arrange
        Aluno alunoMock = new Aluno();
        alunoMock.setId(10);
        alunoMock.setUsuario(usuarioMock);

        when(alunoRepository.findAll()).thenReturn(List.of(alunoMock));

        Disciplina disciplina = new Disciplina();
        disciplina.setNome("Filosofia");
        Turma turma = new Turma();
        turma.setDisciplina(disciplina);

        Matricula matriculaMock = new Matricula();
        matriculaMock.setId(100);
        matriculaMock.setAluno(alunoMock);
        matriculaMock.setTurma(turma);

        when(matriculaRepository.findAll()).thenReturn(List.of(matriculaMock));

        // Risco BAIXO
        AlertaRiscoDTO riscoBaixo = new AlertaRiscoDTO("BAIXO", BigDecimal.TEN, 0, List.of("Tudo bem"));
        when(analiseRiscoService.analisarRiscoMatricula(100)).thenReturn(riscoBaixo);

        // Act
        UsuarioPerfilDTO perfil = usuarioService.obterMeuPerfil();

        // Assert
        assertFalse(perfil.emRisco(), "A flag emRisco deveria ser FALSE, pois o risco é BAIXO.");
    }

    @Test
    @DisplayName("alterarMinhaSenha - Deve lançar exceção se a senha atual estiver incorreta")
    void alterarSenhaDeveFalharComSenhaAtualIncorreta() {
        // Arrange
        AlterarSenhaDTO dto = new AlterarSenhaDTO("senha_errada", "nova_senha");

        // O Mockito simula o Bcrypt dizendo que as senhas não batem
        when(passwordEncoder.matches("senha_errada", "senha_hash_banco")).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.alterarMinhaSenha(dto);
        });

        assertEquals("A senha atual informada está incorreta.", exception.getMessage());

        // Garante que não tentou salvar no banco
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("alterarMinhaSenha - Deve salvar a nova senha com sucesso")
    void alterarSenhaDeveSalvarQuandoSenhaAtualCorreta() {
        // Arrange
        AlterarSenhaDTO dto = new AlterarSenhaDTO("senha_certa", "nova_senha");

        when(passwordEncoder.matches("senha_certa", "senha_hash_banco")).thenReturn(true);
        when(passwordEncoder.encode("nova_senha")).thenReturn("nova_senha_hash");

        // Act
        usuarioService.alterarMinhaSenha(dto);

        // Assert
        assertEquals("nova_senha_hash", usuarioMock.getSenha());
        verify(usuarioRepository, times(1)).save(usuarioMock);
    }
}