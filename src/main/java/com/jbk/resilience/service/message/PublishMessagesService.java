package com.jbk.resilience.service.message;

import com.jbk.resilience.entitie.MessageEvent;
import com.jbk.resilience.enuns.EventType;
import com.jbk.resilience.interfaces.IProcessEvent;
import com.jbk.resilience.service.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class PublishMessagesService {

    private final RabbitService rabbitService;

    @Autowired
    public PublishMessagesService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }


    public void sendMessage(String message) {
        MessageEvent event = new MessageEvent(UUID.randomUUID().toString(), message, EventType.TESTE_MENSAGEM);

        this.rabbitService.handleMessage(event.getEventType().name(), event, IProcessEvent.class.getDeclaredMethods()[0].toString());
    }

    public void sendMessageDelayed(String message) {
        MessageEvent event = new MessageEvent(UUID.randomUUID().toString(), message, EventType.TESTE_MENSAGEM);

        this.rabbitService.handleMessageDelayTime(event.getEventType().name(), event, IProcessEvent.class.getDeclaredMethods()[0].toString(), 60000);
    }
}
