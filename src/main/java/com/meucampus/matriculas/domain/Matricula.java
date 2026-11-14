package com.meucampus.matriculas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "É necessário informar o ID do aluno.")
    private Long alunoId;

    @NotNull(message = "É necessário informar o ID da turma.")
    private Long turmaId;

    @NotNull(message = "É necessário informar a data da matrícula.")
    private LocalDate dataMatricula;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SituacaoMatricula situacao = SituacaoMatricula.ATIVA;

}