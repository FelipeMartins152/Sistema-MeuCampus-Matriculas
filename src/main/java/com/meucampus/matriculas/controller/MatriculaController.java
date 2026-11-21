package com.meucampus.matriculas.controller;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.dto.MatriculaDetalhesDTO;
import com.meucampus.matriculas.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

    private final CadastrarMatriculaService cadastrarMatriculaService;
    private final BuscarMatriculaPorIdService buscarMatriculaPorIdService;
    private final ListarMatriculasService listarMatriculasService;
    private final AtualizarMatriculaService atualizarMatriculaService;
    private final ExcluirMatriculaService excluirMatriculaService;

    public MatriculaController(
            CadastrarMatriculaService cadastrarMatriculaService,
            BuscarMatriculaPorIdService buscarMatriculaPorIdService,
            ListarMatriculasService listarMatriculasService,
            AtualizarMatriculaService atualizarMatriculaService,
            ExcluirMatriculaService excluirMatriculaService) {
        this.cadastrarMatriculaService = cadastrarMatriculaService;
        this.buscarMatriculaPorIdService = buscarMatriculaPorIdService;
        this.listarMatriculasService = listarMatriculasService;
        this.atualizarMatriculaService = atualizarMatriculaService;
        this.excluirMatriculaService = excluirMatriculaService;
    }

    @GetMapping
    public ResponseEntity<Page<Matricula>> listar(
            @RequestParam(name = "alunoId", required = false) Long alunoId,
            @RequestParam(name = "turmaId", required = false) Long turmaId,
            @RequestParam(required = false) String situacao, Pageable pageable) {

        SituacaoMatricula situacaoConvertida = null;
        if (situacao != null && !situacao.isBlank()) {
            try {
                situacaoConvertida = SituacaoMatricula.valueOf(situacao.trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(
                        "Situação inválida. Use ATIVA ou TRANCADA.");
            }
        }

        Page<Matricula> matriculas = listarMatriculasService.executar(
                alunoId,
                turmaId,
                situacaoConvertida,
                pageable);
        return ResponseEntity.ok(matriculas);
    }

    @GetMapping("/{id}/detalhes")
    public ResponseEntity<MatriculaDetalhesDTO> buscarDetalhes(@PathVariable Long id) {
        return ResponseEntity.ok(buscarMatriculaPorIdService.buscarDetalhes(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Matricula> buscarPorId(@PathVariable long id) {
        return buscarMatriculaPorIdService.executar(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Matricula> criar(@Valid @RequestBody Matricula matricula) {
        Matricula novaMatricula = cadastrarMatriculaService.executar(matricula);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaMatricula); // HTTP 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Matricula> atualizar(@PathVariable long id, @Valid @RequestBody Matricula matricula) {
        return atualizarMatriculaService.executar(id, matricula)
                .map(ResponseEntity::ok) 
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable long id) {
        boolean removido = excluirMatriculaService.executar(id);
        if (removido) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}