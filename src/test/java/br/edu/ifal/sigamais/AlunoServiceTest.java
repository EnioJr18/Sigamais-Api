package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.AlunoRequestDTO;
import br.edu.ifal.sigamais.dto.AlunoResponseDTO;
import br.edu.ifal.sigamais.model.Aluno;
import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.repository.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository repository;

    @InjectMocks
    private AlunoService service;

    @Test
    void deveSalvarAlunoComSucesso() {

        // 1. PREPARAÇÃO (Arrange)
        // O Record agora exige os 8 campos (Usuário + Aluno)
        AlunoRequestDTO request = new AlunoRequestDTO(
                "Enio Eduardo",           // nome
                "12345678900",            // cpf
                "enio@email.com",
                "senha123",
                "2024001",
                "Sistemas de Informação",
                new BigDecimal("2500.00"),
                2024
        );

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setNome(request.nome());


        Aluno alunoSalvoNoBanco = new Aluno();
        alunoSalvoNoBanco.setId(1);
        alunoSalvoNoBanco.setUsuario(usuarioSalvo);
        alunoSalvoNoBanco.setMatricula(request.matricula());
        alunoSalvoNoBanco.setCurso(request.curso());
        alunoSalvoNoBanco.setStatus("ATIVO");

        Mockito.when(repository.save(Mockito.any(Aluno.class))).thenReturn(alunoSalvoNoBanco);

        AlunoResponseDTO response = service.salvar(request);

        assertNotNull(response, "O response não deveria ser nulo");
        assertEquals(1, response.id(), "O ID não foi gerado pelo banco");
        assertEquals(request.matricula(), response.matricula(), "A matricula não foi mapeada corretamente");
        assertEquals("ATIVO", response.status(), "O status inicial do aluno deveria ser ATIVO");

        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Aluno.class));
    }
}