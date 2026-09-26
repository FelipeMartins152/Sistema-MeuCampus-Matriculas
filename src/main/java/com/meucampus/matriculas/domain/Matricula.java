package com.meucampus.matriculas.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Matricula {
    private Long id;

    @JsonProperty("aluno_id")
    private Long alunoId;

    @JsonProperty("turma_id")
    private Long turmaId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("data_matricula")
    private LocalDate dataMatricula;

    private Situacao situacao;
}