package com.meucampus.matriculas.controller;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<Matricula>> listar(
            @RequestParam(name = "aluno_id", required = false) Long alunoId,
            @RequestParam(name = "turma_id", required = false) Long turmaId,
            @RequestParam(required = false) SituacaoMatricula situacao) {

        List<Matricula> matriculas = listarMatriculasService.executar(alunoId, turmaId, situacao);
        return ResponseEntity.ok(matriculas);
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