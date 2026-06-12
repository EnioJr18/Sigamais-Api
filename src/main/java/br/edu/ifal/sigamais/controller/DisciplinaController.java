package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.DisciplinaRequestDTO;
import br.edu.ifal.sigamais.dto.DisciplinaResponseDTO;
import br.edu.ifal.sigamais.service.DisciplinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController {

    @Autowired
    private DisciplinaService service;

    @PostMapping
    public ResponseEntity<DisciplinaResponseDTO> cadastrar(@RequestBody DisciplinaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<DisciplinaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }
}