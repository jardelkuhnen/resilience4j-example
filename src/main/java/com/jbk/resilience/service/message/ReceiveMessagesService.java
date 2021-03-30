package com.jbk.resilience.service.message;

import com.jbk.resilience.enuns.EventType;
import com.jbk.resilience.interfaces.IEvent;
import com.jbk.resilience.interfaces.IProcessEvent;
import com.jbk.resilience.service.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Slf4j
@Service
public class ReceiveMessagesService implements IProcessEvent {

    private final RabbitService rabbitService;
    private final Integer QTD_CONSUMERS = 2;

    @Autowired
    public ReceiveMessagesService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @PostConstruct
    public void listenMessages() {
        String handlerName = this.getClass().getName();
        log.info("Registering the event: " + handlerName);

        this.rabbitService.registerQueue(EventType.TESTE_MENSAGEM.name(), handlerName, this::processEvents, QTD_CONSUMERS);
    }

    @Override
    public void processEvents(IEvent event) {
        log.info("Received message time: ".concat(LocalDateTime.now().toString()));
        log.info("Processing event received. " + event.getType() + "Class: " + event.getClass());
        log.info(event.getMessage());
    }

}
