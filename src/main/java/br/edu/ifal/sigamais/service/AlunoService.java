package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.AlunoRequestDTO;
import br.edu.ifal.sigamais.dto.AlunoResponseDTO;
import br.edu.ifal.sigamais.model.Aluno;
import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository repository;

    public AlunoResponseDTO salvar(AlunoRequestDTO dto) {

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setCpf(dto.cpf());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setPerfil("ALUNO");

        Aluno aluno = new Aluno();
        aluno.setMatricula(dto.matricula());
        aluno.setCurso(dto.curso());
        aluno.setRendaFamiliar(dto.rendaFamiliar());
        aluno.setAnoIngresso(dto.anoIngresso());
        aluno.setStatus("ATIVO");

        aluno.setUsuario(usuario);

        Aluno alunoSalvo = repository.save(aluno);

        return new AlunoResponseDTO(
                alunoSalvo.getId(),
                alunoSalvo.getUsuario().getNome(),
                alunoSalvo.getMatricula(),
                alunoSalvo.getCurso(),
                alunoSalvo.getStatus()
        );
    }
}