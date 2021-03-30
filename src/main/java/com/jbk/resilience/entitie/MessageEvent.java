package com.jbk.resilience.entitie;

import com.jbk.resilience.enuns.EventType;
import com.jbk.resilience.interfaces.IEvent;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent implements IEvent {

    private String uuid;
    private String mensagem;
    private EventType eventType;

    @Override
    public EventType getType() {
        return this.eventType;
    }

    @Override
    public String getMessage() {
        return this.mensagem;
    }
}
