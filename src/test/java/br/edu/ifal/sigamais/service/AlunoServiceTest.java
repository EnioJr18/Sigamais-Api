package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.AlunoRequestDTO;
import br.edu.ifal.sigamais.dto.AlunoResponseDTO;
import br.edu.ifal.sigamais.model.Aluno;
import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.repository.AlunoRepository;
import br.edu.ifal.sigamais.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @InjectMocks
    private AlunoService alunoService;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve cadastrar um Aluno e criar seu Usuário com sucesso")
    void deveCadastrarAlunoComSucesso() {
        AlunoRequestDTO requestDTO = new AlunoRequestDTO(
                "Enio Jr", "123.456.789-00", "enio@ifal.edu.br",
                "senha123", "2024001", "Sistemas de Informação",
                BigDecimal.valueOf(2500.0), 2024
        );

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1);
        usuarioSalvo.setNome("Enio Jr");

        Aluno alunoSalvo = new Aluno();
        alunoSalvo.setId(1);
        alunoSalvo.setUsuario(usuarioSalvo);
        alunoSalvo.setMatricula("2024001");
        alunoSalvo.setStatus("ATIVO");

        Mockito.lenient().when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);
        Mockito.when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoSalvo);

        AlunoResponseDTO response = alunoService.salvar(requestDTO);

        assertNotNull(response);
        assertEquals("2024001", response.matricula());
    }
}