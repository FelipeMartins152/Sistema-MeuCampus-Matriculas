package com.meucampus.matriculas.client;

import com.meucampus.matriculas.dto.TurmaDTO;
import com.meucampus.matriculas.exception.RecursoRemotoNaoEncontradoException;
import com.meucampus.matriculas.exception.ServicoIndisponivelException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class TurmasClient {

    private final RestClient restClient;

    public TurmasClient(@Qualifier("turmasRestClient") RestClient restClient) {

        this.restClient = restClient;

    }

    public TurmaDTO buscarTurma(Long id) {

        try {
            return restClient.get()
                    .uri("/turmas/{id}", id)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 404) {
                            throw new RecursoRemotoNaoEncontradoException(
                                    "Turma com ID " + id + " não foi encontrada.");
                        }
                        throw new RestClientException("Erro ao consultar o serviço de Turmas.");
                    })
                    .body(TurmaDTO.class);
        } catch (RecursoRemotoNaoEncontradoException exception) {
            throw exception;
        } catch (ResourceAccessException exception) {
            throw new ServicoIndisponivelException(
                    "O serviço de Turmas está fora do ar ou não respondeu a tempo.");
        } catch (RestClientException exception) {
            throw new ServicoIndisponivelException(
                    "Falha ao consultar o serviço de Turmas.");
        }

    }
    
}
