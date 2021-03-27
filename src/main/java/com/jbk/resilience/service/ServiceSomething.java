package com.jbk.resilience.service;

import com.jbk.resilience.interfaces.AbstractCallback;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.vavr.control.Try;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Arrays;

@Service
public class ServiceSomething extends CommonsService implements AbstractCallback {

    private static final String BACKEND = "backend";

    @CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
    public String doSomething() {

        return Try.of(this::execute).onFailure(this::fallback).get();
    }

    private String fallback(Throwable ex) {
        System.out.println("Exception -> " + ex);
        System.out.println("Message -> " + ex.getMessage());
        System.out.println("Exception Tostrinh -> "+ex.toString());
        System.out.println("Cause -> "+ex.getCause());
        System.out.println("LocalizedMessage ->"+ex.getLocalizedMessage());
        System.out.println(Arrays.stream(ex.getStackTrace()).findAny().get().getMethodName());
        System.out.println(Arrays.stream(ex.getStackTrace()).findAny().get().getLineNumber());
        System.out.println(Arrays.stream(ex.getStackTrace()).findAny().get().getClassName());
        System.err.println("passei aqui throwable");
        return "Mas aqui nos se recupera doido";
    }

    @Override
    @CircuitBreaker(name = BACKEND)
    @Bulkhead(name = BACKEND)
    @Retry(name = BACKEND)
    public String execute() {
        throw new RuntimeException("Deu um pau aqui mano");
    }

    public String fallback(HttpServerErrorException e) {
        System.err.println("passei aqui");
        return "Mas aqui nos se recupera doido";
    }


}
