package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.AlertaRiscoDTO;
import br.edu.ifal.sigamais.dto.MatriculaRequestDTO;
import br.edu.ifal.sigamais.dto.MatriculaResponseDTO;
import br.edu.ifal.sigamais.service.AnaliseRiscoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.edu.ifal.sigamais.service.MatriculaService;

import java.util.List;

@RestController
@RequestMapping("/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService service;
    private final AnaliseRiscoService analiseRiscoService;


    @PostMapping
    public MatriculaResponseDTO realizarMatricula(@RequestBody MatriculaRequestDTO request) {
        return service.realizarMatricula(request);
    }

    @GetMapping
    public ResponseEntity<List<MatriculaResponseDTO>> listarMatriculas() {
        return ResponseEntity.ok(service.listarMatriculas());
    }


    @GetMapping("/{id}/risco")
    public ResponseEntity<AlertaRiscoDTO> analisarRisco(@PathVariable Integer id) {
        // Chama o método novo e devolve o DTO completo
        return ResponseEntity.ok(analiseRiscoService.analisarRiscoMatricula(id));
    }
}

