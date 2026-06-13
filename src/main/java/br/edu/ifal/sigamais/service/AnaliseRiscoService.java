package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.strategy.RiscoStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnaliseRiscoService {

    @Autowired
    private List<RiscoStrategy> estrategiasDeRisco;

    public String analisarRiscoGlobal(Integer matriculaId) {
        boolean temRiscoMedio = false;

        for (RiscoStrategy estrategia : estrategiasDeRisco) {
            String riscoCalculado = estrategia.avaliarRisco(matriculaId);

            if ("ALTO".equals(riscoCalculado)) {
                return "ALTO";
            }
            if ("MEDIO".equals(riscoCalculado)) {
                temRiscoMedio = true;
            }
        }

        return temRiscoMedio ? "MEDIO" : "BAIXO";
    }
}