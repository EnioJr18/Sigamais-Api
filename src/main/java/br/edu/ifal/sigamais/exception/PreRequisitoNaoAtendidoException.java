package br.edu.ifal.sigamais.exception;

public class PreRequisitoNaoAtendidoException extends RuntimeException {

    public PreRequisitoNaoAtendidoException(String mensagem) {
        super(mensagem);
    }
}