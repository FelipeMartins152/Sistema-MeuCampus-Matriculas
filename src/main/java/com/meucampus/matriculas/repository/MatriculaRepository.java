package com.meucampus.matriculas.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meucampus.matriculas.domain.Matricula;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MatriculaRepository {

    private static final File ARQUIVO_MATRICULAS = new File ("dados/matriculas.json");
    private final ObjectMapper objectMapper;
    private final List<Matricula> matriculas = new ArrayList<>();
    private long proximoId = 1;

    public MatriculaRepository(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void carregar(){
        try{
            if(!ARQUIVO_MATRICULAS.exists()){
                ARQUIVO_MATRICULAS.getParentFile().mkdirs();
                objectMapper.writeValue(ARQUIVO_MATRICULAS, List.of());
            }

            matriculas.addAll(objectMapper.readValue(ARQUIVO_MATRICULAS, new TypeReference<List<Matricula>>() {}));
            proximoId = matriculas.stream().mapToLong(Matricula::getId).max().orElse(0) + 1;
        }catch(IOException e){
            throw new UncheckedIOException("Erro ao ler arquivo de matrículas", e);
        }
    }

    private void gravar(){
        try{
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(ARQUIVO_MATRICULAS, matriculas);
        }catch(IOException e){
            throw new UncheckedIOException("Erro ao gravar no arquivo de matrículas", e);
        }
    }

    public List<Matricula> listar(){
        return new ArrayList<>(matriculas);
    }

    public List<Matricula> buscarPorIdAluno(long alunoId){
        return matriculas.stream()
                .filter(m -> m.getAlunoId().equals(alunoId))
                .toList();
    }

    public List<Matricula> buscarPorIdTurma(long turmaId){
        return matriculas.stream()
                .filter(m -> m.getTurmaId().equals(turmaId))
                .toList();
    }

    public Optional<Matricula> buscarPorId(long id){
        return matriculas.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }

    public Optional<Matricula> atualizar(Matricula matricula){
        Optional<Matricula> existente = matriculas.stream()
                .filter(m -> m.getId().equals(matricula.getId()))
                .findFirst();

        if(existente.isEmpty())
            return Optional.empty();

        matriculas.remove(existente.get());
        matriculas.add(matricula);
        gravar();
        return Optional.of(matricula);
    }

    public Matricula cadastrar(Matricula matricula){
        matricula.setId(proximoId);
        matriculas.add(matricula);
        gravar();
        proximoId ++;
        return matricula;
    }

    public boolean remover(long id){
        boolean removeu = matriculas.removeIf(m -> m.getId().equals(id));
        if(removeu)
            gravar();
        return removeu;
    }
}