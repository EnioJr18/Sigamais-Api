package br.edu.ifal.sigamais.strategy;

public interface RiscoStrategy {
    // Retorna "ALTO", "MEDIO" ou "BAIXO"
    String avaliarRisco(Integer matriculaId);
}