package com.meucampus.matriculas.config;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.meucampus.matriculas.domain.SituacaoMatricula.*;

@Component
@RequiredArgsConstructor
public class RegistrosDeExemplo implements CommandLineRunner{

    private final MatriculaRepository matriculaRepository;

    public void run(String... args){

        if(matriculaRepository.count() == 0) {
            matriculaRepository.save(new Matricula(null, 1L, 1L, LocalDate.of(2026, 9, 20), ATIVA));
            matriculaRepository.save(new Matricula(null, 2L, 2L, LocalDate.of(2026, 9, 10), TRANCADA));
            matriculaRepository.save(new Matricula(null, 3L, 1L, LocalDate.of(2026, 9, 19), ATIVA));
            matriculaRepository.save(new Matricula(null, 4L, 2L, LocalDate.of(2026, 9, 17), ATIVA));
            matriculaRepository.save(new Matricula(null, 5L, 1L, LocalDate.of(2026, 9, 15), TRANCADA));
            matriculaRepository.save(new Matricula(null, 5L, 2L, LocalDate.of(2026, 9, 16), ATIVA));
            matriculaRepository.save(new Matricula(null, 7L, 1L, LocalDate.of(2026, 9, 26), TRANCADA));
            matriculaRepository.save(new Matricula(null, 3L, 2L, LocalDate.of(2026, 9, 28), ATIVA));
            matriculaRepository.save(new Matricula(null, 1L, 3L, LocalDate.of(2026, 9, 28), ATIVA));
            matriculaRepository.save(new Matricula(null, 2L, 3L, LocalDate.of(2026, 9, 30), ATIVA));
        }

    }
}