package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.ProfessorRequestDTO;
import br.edu.ifal.sigamais.dto.ProfessorResponseDTO;
import br.edu.ifal.sigamais.model.Professor;
import br.edu.ifal.sigamais.model.Usuario; // Certifique-se de importar o Enum/Perfil se existir
import br.edu.ifal.sigamais.repository.ProfessorRepository;
import br.edu.ifal.sigamais.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ProfessorResponseDTO salvar(ProfessorRequestDTO dto) {

        // 1. Instancia e salva a entidade central (Usuario)
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setCpf(dto.cpf());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setPerfil("PROFESSOR");

        usuario = usuarioRepository.save(usuario); // Salva no banco primeiro

        // 2. Instancia e salva a entidade específica (Professor) vinculada
        Professor professor = new Professor();
        professor.setUsuario(usuario);
        professor.setTitulacao(dto.titulacao());

        Professor salvo = professorRepository.save(professor);

        // 3. Devolve a resposta bonitinha
        return new ProfessorResponseDTO(salvo.getId(), usuario.getNome(), usuario.getEmail(), salvo.getTitulacao());
    }

    public List<ProfessorResponseDTO> listarTodos() {
        return professorRepository.findAll().stream()
                .map(p -> new ProfessorResponseDTO(
                        p.getId(),
                        p.getUsuario().getNome(),
                        p.getUsuario().getEmail(),
                        p.getTitulacao()
                ))
                .toList();
    }
}