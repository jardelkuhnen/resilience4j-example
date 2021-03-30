package com.jbk.resilience.interfaces;

@FunctionalInterface
public interface ICallbackEvent {

    void handle(IEvent event) throws Exception;

}
