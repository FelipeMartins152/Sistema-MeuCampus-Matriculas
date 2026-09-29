package com.meucampus.matriculas.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Matricula {

    private Long id;

    @NotNull(message = "É necessário informar o ID do aluno.")
    @JsonProperty("aluno_id")
    private Long alunoId;

    @NotNull(message = "É necessário informar o ID da turma.")
    @JsonProperty("turma_id")
    private Long turmaId;

    @NotNull(message = "É necessário informar a data da matrícula.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("data_matricula")
    private LocalDate dataMatricula;

    @JsonProperty("situacao")
    @Builder.Default
    private SituacaoMatricula situacao = SituacaoMatricula.ATIVA;

}