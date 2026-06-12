package br.edu.ifal.sigamais.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<?> handleNotFound(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "timestamp", LocalDateTime.now(),
            "status", 404,
            "erro", ex.getMessage()
        ));
    }

    @ExceptionHandler(LimitesVagasException.class)
    public ResponseEntity<?> handleLimitesVagas(LimitesVagasException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "erro", ex.getMessage()
        ));
    }

    @ExceptionHandler(PreRequisitoNaoAtendidoException.class)
    public ResponseEntity<?> handlePreRequisitoNaoAtendido(PreRequisitoNaoAtendidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "erro", ex.getMessage()
        ));
    }
}

