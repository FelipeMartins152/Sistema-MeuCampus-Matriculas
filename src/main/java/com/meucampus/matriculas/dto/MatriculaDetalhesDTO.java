package com.meucampus.matriculas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MatriculaDetalhesDTO {

    private Long id;

    @JsonProperty("aluno_id")
    private Long alunoId;

    private String nomeAluno;

    @JsonProperty("turma_id")
    private Long turmaId;

    private String codigoDisciplina;
    private String nomeDisciplina;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("data_matricula")
    private LocalDate dataMatricula;

    private SituacaoMatricula situacao;
    
}
