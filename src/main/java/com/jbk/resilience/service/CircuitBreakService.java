package com.jbk.resilience.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CircuitBreakService {

    protected static final String CIRCUITBREAK_NAME = "CIRCUITO_BEM_LOKO";

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    protected CircuitBreaker circuitBreaker;

    @PostConstruct
    public void init() {
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(CIRCUITBREAK_NAME);
    }


}
