package com.jbk.resilience.enuns;

public enum EventType {

    FALLBACK("Fallback"),
    SUCESS("Sucesso"),
    TESTE_MENSAGEM("testeMensagem");

    private final String message;

    EventType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
