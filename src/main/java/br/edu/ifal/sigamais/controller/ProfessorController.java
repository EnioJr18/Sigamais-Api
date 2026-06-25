package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.ProfessorRequestDTO;
import br.edu.ifal.sigamais.dto.ProfessorResponseDTO;
import br.edu.ifal.sigamais.model.Professor;
import br.edu.ifal.sigamais.service.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService service;

    @PostMapping
    public ResponseEntity<ProfessorResponseDTO> cadastrar(@RequestBody ProfessorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProfessorResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professor> atualizar(@PathVariable Integer id, @RequestBody @Validated ProfessorRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Integer id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}