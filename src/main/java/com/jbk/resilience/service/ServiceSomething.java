package com.jbk.resilience.service;

import com.jbk.resilience.enuns.EventType;
import com.jbk.resilience.entitie.MessageEvent;
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
public class ServiceSomething extends CircuitBreakService implements DefaultServiceMethod<MessageEvent> {

    public MessageEvent doSomethingSucess() {

        Supplier<MessageEvent> decorated = CircuitBreaker.decorateSupplier(circuitBreaker, this::execute);

        MessageEvent event = Try.ofSupplier(decorated).recover(this::fallback).get();

        return event;
    }

    public MessageEvent doSomethingError() {

        Supplier<MessageEvent> decorated = CircuitBreaker.decorateSupplier(circuitBreaker, this::executeError);

        MessageEvent event = Try.ofSupplier(decorated).recover(this::fallback).get();

        return event;
    }

    private MessageEvent fallback(Throwable ex) {
        log.error("Exception -> " + ex);
        log.error("Message -> " + ex.getMessage());
        log.error("Exception Tostrinh -> " + ex.toString());
        log.error("Cause -> " + ex.getCause());
        log.error("LocalizedMessage ->" + ex.getLocalizedMessage());

        String mensagem = ex.getMessage().concat(", mas aqui nos se recupera doido");
        log.error(mensagem);
        return new MessageEvent(UUID.randomUUID().toString(), mensagem, EventType.FALLBACK);
    }

    @Override
    public MessageEvent execute() {
        //Aqui faz a comunicacao com terceiro

        return new MessageEvent(UUID.randomUUID().toString(), "Mensagem de deu boa", EventType.SUCESS);
    }

    public MessageEvent executeError() {
        throw new BusinessException("Deu um pau aqui mano");
    }


}
