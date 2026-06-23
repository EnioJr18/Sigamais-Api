package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.service.AlertaRiscoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alertas-risco")
@RequiredArgsConstructor
public class AlertaRiscoController {

    private final AlertaRiscoService alertaRiscoService;

    @PostMapping("/{matriculaId}/notificar")
    public ResponseEntity<String> notificarCoordenacao(@PathVariable Integer matriculaId) {
        try {
            alertaRiscoService.notificarCoordenacao(matriculaId);
            return ResponseEntity.ok("Coordenação notificada com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<java.util.List<br.edu.ifal.sigamais.dto.AlertaResponseDTO>> listarAlertas() {
        return ResponseEntity.ok(alertaRiscoService.listarTodosAlertas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarAlerta(@PathVariable Integer id, @RequestBody br.edu.ifal.sigamais.dto.AtualizarAlertaDTO dto) {
        alertaRiscoService.atualizarAlerta(id, dto);
        return ResponseEntity.noContent().build();
    }
}