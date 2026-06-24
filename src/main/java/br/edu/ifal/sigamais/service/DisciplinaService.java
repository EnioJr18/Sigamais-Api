package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.DisciplinaRequestDTO;
import br.edu.ifal.sigamais.dto.DisciplinaResponseDTO;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Disciplina;
import br.edu.ifal.sigamais.repository.DisciplinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository repository;

    public DisciplinaResponseDTO salvar(DisciplinaRequestDTO dto) {
        Disciplina disciplina = new Disciplina();
        disciplina.setNome(dto.nome());
        disciplina.setCargaHoraria(dto.cargaHoraria());

        Disciplina salva = repository.save(disciplina);
        return new DisciplinaResponseDTO(salva.getId(), salva.getNome(), salva.getCargaHoraria());
    }

    public List<DisciplinaResponseDTO> listarTodas() {
        return repository.findAll().stream()
                .map(d -> new DisciplinaResponseDTO(d.getId(), d.getNome(), d.getCargaHoraria()))
                .toList();
    }

    public Disciplina buscarEntidadePorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina não encontrada."));
    }

    public Disciplina atualizar(Integer id, DisciplinaRequestDTO dto) {
        Disciplina disciplina = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada."));

        disciplina.setNome(dto.nome());
        disciplina.setCargaHoraria(dto.cargaHoraria());

        return repository.save(disciplina);
    }

    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Disciplina não encontrada.");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Não é possível excluir. Existem turmas vinculadas a esta disciplina.");
        }
    }
}