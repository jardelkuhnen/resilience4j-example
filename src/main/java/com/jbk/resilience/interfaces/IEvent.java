package com.jbk.resilience.interfaces;


import com.jbk.resilience.enuns.EventType;

public interface IEvent {

    EventType getType();

    String getMessage();

}
