package com.jbk.resilience.service;

import com.jbk.resilience.entitie.Event;
import com.jbk.resilience.entitie.EventType;
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
public class ServiceSomething extends CircuitBreakService implements DefaultServiceMethod<Event> {

    public Event doSomethingSucess() {

        run(this::execute, this::deuErro);

        Supplier<Event> decorated = CircuitBreaker.decorateSupplier(circuitBreaker, this::execute);

        Event event = Try.ofSupplier(decorated).recover(this::fallback).get();

        return event;
    }

    public String deuErro() {
        return "Deu erro";
    }

    public Event doSomethingError() {

        Supplier<Event> decorated = CircuitBreaker.decorateSupplier(circuitBreaker, this::executeError);

        Event event = Try.ofSupplier(decorated).recover(this::fallback).get();

        return event;
    }

    private Event fallback(Throwable ex) {
        log.error("Exception -> " + ex);
        log.error("Message -> " + ex.getMessage());
        log.error("Exception Tostrinh -> " + ex);
        log.error("Cause -> " + ex.getCause());
        log.error("LocalizedMessage ->" + ex.getLocalizedMessage());

        String mensagem = ex.getMessage().concat(", mas aqui nos se recupera doido");
        log.error(mensagem);
        return new Event(UUID.randomUUID().toString(), mensagem, EventType.FALLBACK);
    }

    @Override
    public Event execute() {
        //Aqui faz a comunicacao com terceiro

        return new Event(UUID.randomUUID().toString(), "Mensagem de deu boa", EventType.SUCESS);
    }

    public Event executeError() {
        throw new NullPointerException("Deu um pau aqui mano");
    }


    public Event doSomethingErrorBusines() {
        throw new BusinessException("Deu um erro de negocio aqui mano");
    }
}
