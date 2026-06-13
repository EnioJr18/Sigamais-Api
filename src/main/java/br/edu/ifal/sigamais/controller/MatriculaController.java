package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.MatriculaRequestDTO;
import br.edu.ifal.sigamais.dto.MatriculaResponseDTO;
import br.edu.ifal.sigamais.service.AnaliseRiscoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.edu.ifal.sigamais.service.MatriculaService;

@RestController
@RequestMapping("/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService service;

    @Autowired
    private AnaliseRiscoService analiseRiscoService;

    @PostMapping
    public MatriculaResponseDTO realizarMatricula(@RequestBody MatriculaRequestDTO request) {
        return service.realizarMatricula(request);
    }

    @GetMapping("/{id}/risco")
    public ResponseEntity analisarRisco(@PathVariable Integer id) {
        String nivelRisco = analiseRiscoService.analisarRiscoGlobal(id);

        // Vamos devolver um JSON bonitinho {"risco": "ALTO"} para o React ler fácil
        return ResponseEntity.ok(java.util.Map.of("risco", nivelRisco));
    }
}

