package com.meucampus.matriculas.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    @Qualifier("cadastroRestClient")
    public RestClient cadastroRestClient(
            @Value("${servicos.cadastro.url}") String cadastroUrl,
            @Value("${servicos.http.connect-timeout:3000}") long connectTimeout,
            @Value("${servicos.http.read-timeout:3000}") long readTimeout) {
        return criarRestClient(cadastroUrl, connectTimeout, readTimeout);
    }

    @Bean
    @Qualifier("turmasRestClient")
    public RestClient turmasRestClient(
            @Value("${servicos.turmas.url}") String turmasUrl,
            @Value("${servicos.http.connect-timeout:3000}") long connectTimeout,
            @Value("${servicos.http.read-timeout:3000}") long readTimeout) {
        return criarRestClient(turmasUrl, connectTimeout, readTimeout);
    }

    private RestClient criarRestClient(String baseUrl, long connectTimeout, long readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeout));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeout));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
