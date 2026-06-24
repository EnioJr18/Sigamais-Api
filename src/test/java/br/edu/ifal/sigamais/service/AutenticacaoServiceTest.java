package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve retornar UserDetails (Usuario) com sucesso ao buscar email correto")
    void deveRetornarUsuarioComSucesso() {
        // Arrange
        String email = "enio@ifal.edu.br";
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail(email);
        usuarioMock.setSenha("senha123");
        usuarioMock.setPerfil("ALUNO");

        Mockito.when(usuarioRepository.findByEmail(email)).thenReturn(usuarioMock);

        // Act
        UserDetails resultado = autenticacaoService.loadUserByUsername(email);

        // Assert
        assertNotNull(resultado);
        assertEquals(email, resultado.getUsername());
        assertEquals("senha123", resultado.getPassword());
        Mockito.verify(usuarioRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Deve retornar nulo ao buscar email incorreto/inexistente (comportamento atual)")
    void deveRetornarNuloSeEmailIncorreto() {
        // Arrange
        String emailErrado = "errado@ifal.edu.br";
        
        Mockito.when(usuarioRepository.findByEmail(emailErrado)).thenReturn(null);

        // Act
        UserDetails resultado = autenticacaoService.loadUserByUsername(emailErrado);

        // Assert
        assertNull(resultado, "A implementação atual retorna null quando o usuário não é encontrado");
        Mockito.verify(usuarioRepository).findByEmail(emailErrado);
    }

    @Test
    @DisplayName("Deve propagar exceção de banco de dados caso ocorra falha no repositório")
    void devePropagarExcecaoDoRepositorio() {
        // Arrange
        String email = "erro@ifal.edu.br";
        Mockito.when(usuarioRepository.findByEmail(email)).thenThrow(new RuntimeException("Falha na conexão com o banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            autenticacaoService.loadUserByUsername(email);
        });

        assertEquals("Falha na conexão com o banco", exception.getMessage());
        Mockito.verify(usuarioRepository).findByEmail(email);
    }
}

