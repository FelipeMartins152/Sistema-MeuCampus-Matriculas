package com.meucampus.matriculas.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Matricula {
    private Long id;

    @JsonProperty("alunoId")
    private Long alunoId;

    @JsonProperty("turmaId")
    private Long turmaId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataMatricula;

    private Situacao situacao;
}