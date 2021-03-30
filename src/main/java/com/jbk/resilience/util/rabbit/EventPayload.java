package com.jbk.resilience.util.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbk.resilience.interfaces.IEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class EventPayload {

    private String className;
    private String handlerName;
    private String event;

    public EventPayload(IEvent event, String handlerName) {
        this.className = event.getClass().getName();
        this.handlerName = handlerName;
        ObjectMapper mapper = new ObjectMapper();

        try {
            this.event = mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Error to execute the parsing ", e);
        }
    }

}
