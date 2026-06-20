package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.AlunoRequestDTO;
import br.edu.ifal.sigamais.dto.AlunoResponseDTO;
import br.edu.ifal.sigamais.model.Aluno;
import br.edu.ifal.sigamais.repository.AlunoRepository;
import br.edu.ifal.sigamais.service.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService service;

    @Autowired
    private AlunoRepository alunoRepository;

    @GetMapping
    public ResponseEntity<List<Aluno>> listar() {
        return ResponseEntity.ok(alunoRepository.findAll());
    }

    @PostMapping
    public AlunoResponseDTO cadastrarAluno(@RequestBody AlunoRequestDTO request) {
        return service.salvar(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        alunoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}