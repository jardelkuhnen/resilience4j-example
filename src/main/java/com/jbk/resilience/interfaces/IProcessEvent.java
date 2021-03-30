package com.jbk.resilience.interfaces;

@FunctionalInterface
public interface IProcessEvent {

    void processEvents(IEvent event);

}
