package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.ProfessorRequestDTO;
import br.edu.ifal.sigamais.dto.ProfessorResponseDTO;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Professor;
import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.repository.ProfessorRepository;
import br.edu.ifal.sigamais.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public ProfessorResponseDTO salvar(ProfessorRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        Professor professor = new Professor();
        professor.setUsuario(usuario);
        professor.setTitulacao(dto.titulacao());

        Professor salvo = professorRepository.save(professor);
        return new ProfessorResponseDTO(salvo.getId(), usuario.getNome(), salvo.getTitulacao());
    }

    public List<ProfessorResponseDTO> listarTodos() {
        return professorRepository.findAll().stream()
                .map(p -> new ProfessorResponseDTO(
                        p.getId(),
                        p.getUsuario().getNome(),
                        p.getTitulacao()
                ))
                .toList();
    }
}