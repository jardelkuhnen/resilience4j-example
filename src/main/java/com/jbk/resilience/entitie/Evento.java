package com.jbk.resilience.entitie;

import lombok.*;

@Data
@AllArgsConstructor
public class Evento {

    private String uuid;
    private String mensagem;
    private EventType eventType;

}
