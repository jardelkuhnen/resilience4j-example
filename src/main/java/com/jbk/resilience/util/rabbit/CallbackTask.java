package com.jbk.resilience.util.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbk.resilience.interfaces.ICallbackEvent;
import com.jbk.resilience.interfaces.IEvent;
import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
@Getter
@Setter
public class CallbackTask implements Runnable {

    private long deliveryTag;
    private Channel channel;
    private byte[] body;
    int delay;
    private ICallbackEvent callback;

    private String handlerName;


    public CallbackTask(long deliveryTag, Channel channel, byte[] body, int delay, ICallbackEvent callback, String handlerName) {
        this.deliveryTag = deliveryTag;
        this.channel = channel;
        this.body = body;
        this.delay = delay;
        this.callback = callback;
        this.handlerName = handlerName;
    }

    @Override
    public void run() {
        try {

            log.info("Iniciado callback para disparo do evento");

            ObjectMapper mapper = new ObjectMapper();
            String message = new String(body, StandardCharsets.UTF_8);
            EventPayload payload = mapper.readValue(message, EventPayload.class);

            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Class<?> clazz = Class.forName(payload.getClassName());

            IEvent event = (IEvent) mapper.readValue(payload.getEvent(), clazz);

            callback.handle(event);

            channel.basicAck(deliveryTag, false);

            log.info("Evento disparado: " + event.getType());

        } catch (JsonProcessingException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
