package com.meucampus.matriculas.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class TurmaDTO {

    private Long id;
    private String codigo;

    @JsonAlias("nomeDisciplina")
    private String nome;

    @JsonAlias({"vagas", "quantidadeVagas", "numeroVagas"})
    private Integer vagas;
    
}
