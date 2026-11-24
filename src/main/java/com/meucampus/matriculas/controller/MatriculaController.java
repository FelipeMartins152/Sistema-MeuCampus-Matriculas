package com.meucampus.matriculas.controller;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.dto.MatriculaDetalhesDTO;
import com.meucampus.matriculas.exception.RecursoNaoEncontradoException;
import com.meucampus.matriculas.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/matriculas")
@Tag(name = "Matrículas", description = "Operações sobre matrículas")
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
    @Operation(summary = "Lista matrículas com filtros e paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de matrículas encontrada"),
            @ApiResponse(responseCode = "400", description = "Situação ou parâmetro inválido")
    })

    public ResponseEntity<Page<Matricula>> listar(
        @Parameter(description = "ID do aluno")
        @RequestParam(name = "alunoId", required = false) Long alunoId,
        @Parameter(description = "ID da turma")
        @RequestParam(name = "turmaId", required = false) Long turmaId,
        @Parameter(description = "Situação (ATIVA ou TRANCADA, maiúscula ou minúscula)",
                   schema = @Schema(allowableValues = {"ATIVA", "TRANCADA", "ativa", "trancada"}))
        @RequestParam(required = false) String situacao,
        @ParameterObject Pageable pageable) {

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
    @Operation(summary = "Consulta os dados compostos de uma matrícula")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalhes encontrados"),
        @ApiResponse(responseCode = "400", description = "Aluno ou turma não existe mais no serviço externo"),
        @ApiResponse(responseCode = "404", description = "Matrícula não encontrada"),
        @ApiResponse(responseCode = "503", description = "Cadastro ou Turmas indisponível")
})
    public ResponseEntity<MatriculaDetalhesDTO> buscarDetalhes(@PathVariable Long id) {
        return ResponseEntity.ok(buscarMatriculaPorIdService.buscarDetalhes(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma matrícula pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matrícula encontrada"),
            @ApiResponse(responseCode = "404", description = "Matrícula não encontrada")
    })
    public ResponseEntity<Matricula> buscarPorId(@PathVariable long id) {
        Matricula matricula = buscarMatriculaPorIdService.executar(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "A matrícula " + id + " não foi encontrada."));
        return ResponseEntity.ok(matricula);
    }

    @PostMapping
    @Operation(summary = "Cadastra uma matrícula ativa")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Matrícula cadastrada"),
            @ApiResponse(responseCode = "400", description = "Aluno ou turma inexistente"),
            @ApiResponse(responseCode = "409", description = "Matrícula duplicada ou turma lotada"),
            @ApiResponse(responseCode = "503", description = "Cadastro ou Turmas indisponível")
    })
    public ResponseEntity<Matricula> criar(@Valid @RequestBody Matricula matricula) {
        Matricula novaMatricula = cadastrarMatriculaService.executar(matricula);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaMatricula);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza ou reativa uma matrícula")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matrícula atualizada"),
            @ApiResponse(responseCode = "400", description = "Aluno ou turma inexistente"),
            @ApiResponse(responseCode = "404", description = "Matrícula não encontrada"),
            @ApiResponse(responseCode = "409", description = "Matrícula duplicada ou turma lotada"),
            @ApiResponse(responseCode = "503", description = "Cadastro ou Turmas indisponível")
    })
    public ResponseEntity<Matricula> atualizar(@PathVariable long id, @Valid @RequestBody Matricula matricula) {
        return ResponseEntity.ok(atualizarMatriculaService.executar(id, matricula));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma matrícula trancada")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Matrícula removida"),
            @ApiResponse(responseCode = "404", description = "Matrícula não encontrada"),
            @ApiResponse(responseCode = "409", description = "Só é possível excluir matrículas trancadas")
    })
    public ResponseEntity<Void> deletar(@PathVariable long id) {
        excluirMatriculaService.executar(id);
        return ResponseEntity.noContent().build();
    }
}