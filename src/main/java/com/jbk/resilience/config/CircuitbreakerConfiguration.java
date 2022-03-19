package com.jbk.resilience.config;

import com.jbk.resilience.exceptions.BusinessException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitbreakerConfiguration {

    public CircuitBreakerConfig createCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .slidingWindowSize(5) // Janela de tempo para o calculo das requisicoes
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // Tipo de calculo contagem ou tempo
                .failureRateThreshold(2) // Quantidade de falhas para abertura do circuito
                .slowCallRateThreshold(2) // Quantas chamadas permitidas de erro ate abertura do circuito
                .waitDurationInOpenState(Duration.ofMillis(100000)) // tempo de circuito aberto
                .permittedNumberOfCallsInHalfOpenState(2) // Quantidade de calls durante HALF_OPEN
                .minimumNumberOfCalls(5) // Número minimo de requests durante `slidingWindowSize`
                .recordExceptions(Exception.class) // Exceções que serao escutadas
                .ignoreExceptions(BusinessException.class) // Exceções que serao ignoradas
                .build();
    }


    @Bean
    public CircuitBreakerRegistry createCircuitBreakerRegistry() {
        return CircuitBreakerRegistry.of(this.createCircuitBreakerConfig());
    }


}
