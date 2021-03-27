package com.jbk.resilience.service;

import com.jbk.resilience.entitie.EventType;
import com.jbk.resilience.entitie.Evento;
import com.jbk.resilience.exceptions.BusinessException;
import com.jbk.resilience.interfaces.DefaultServiceMethod;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Service
public class ServiceSomething extends CircuitBreakService implements DefaultServiceMethod<Evento> {

    public Evento doSomethingSucess() {

        Supplier<Evento> decorated = CircuitBreaker.decorateSupplier(circuitBreaker, this::execute);

        Evento event = Try.ofSupplier(decorated).recover(this::fallback).get();

        return event;
    }

    public Evento doSomethingError() {

        Supplier<Evento> decorated = CircuitBreaker.decorateSupplier(circuitBreaker, this::executeError);

        Evento event = Try.ofSupplier(decorated).recover(this::fallback).get();

        return event;
    }

    private Evento fallback(Throwable ex) {
        log.error("Exception -> " + ex);
        log.error("Message -> " + ex.getMessage());
        log.error("Exception Tostrinh -> " + ex.toString());
        log.error("Cause -> " + ex.getCause());
        log.error("LocalizedMessage ->" + ex.getLocalizedMessage());

        String mensagem = ex.getMessage().concat(", mas aqui nos se recupera doido");
        log.error(mensagem);
        return new Evento(UUID.randomUUID().toString(), mensagem, EventType.FALLBACK);
    }

    @Override
    public Evento execute() {
        //Aqui faz a comunicacao com terceiro

        return new Evento(UUID.randomUUID().toString(), "Mensagem de deu boa", EventType.SUCESS);
    }

    public Evento executeError() {
        throw new BusinessException("Deu um pau aqui mano");
    }


}
