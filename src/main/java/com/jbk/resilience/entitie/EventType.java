package com.jbk.resilience.entitie;

public enum EventType {

    FALLBACK("Fallback"),
    SUCESS("Sucesso");

    private final String message;

    EventType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
