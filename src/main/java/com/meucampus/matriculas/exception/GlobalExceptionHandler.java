package com.meucampus.matriculas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.data.mapping.PropertyReferenceException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoRemotoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRecursoRemotoNaoEncontrado(
            RecursoRemotoNaoEncontradoException ex) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
    }

    @ExceptionHandler(ServicoIndisponivelException.class)
    public ResponseEntity<Map<String, Object>> handleServicoIndisponivel(
            ServicoIndisponivelException ex) {
        return criarResposta(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex) {
        return criarResposta(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Erro de validação dos campos.");

        return criarResposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido ou malformado.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        String mensagem = "Parâmetro inválido: " + ex.getName();
        return criarResposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return criarResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, Object>> handleRegraNegocioException(RegraNegocioException ex) {
        return criarResposta(HttpStatus.CONFLICT, ex.getMessage());
    }

@ExceptionHandler(PropertyReferenceException.class)
public ResponseEntity<Map<String, Object>> handlePropertyReference(PropertyReferenceException ex) {
    return criarResposta(HttpStatus.BAD_REQUEST,
            "Campo de ordenação inválido: " + ex.getPropertyName());
}

    private ResponseEntity<Map<String, Object>> criarResposta(
            HttpStatus status,
            String mensagem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", mensagem);

        return ResponseEntity.status(status).body(body);
    }
    
}