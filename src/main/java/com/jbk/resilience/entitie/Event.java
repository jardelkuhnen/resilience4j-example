package com.jbk.resilience.entitie;

import lombok.*;

@Data
@AllArgsConstructor
public class Event {

    private String uuid;
    private String mensagem;
    private EventType eventType;

}
