package br.edu.ifal.sigamais.repository;

import br.edu.ifal.sigamais.model.HistoricoAlertaRisco;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoricoAlertaRiscoRepository extends JpaRepository<HistoricoAlertaRisco, Integer> {
    List<HistoricoAlertaRisco> findByAlertaRiscoIdOrderByCriadoEmAsc(Integer alertaRiscoId);
}