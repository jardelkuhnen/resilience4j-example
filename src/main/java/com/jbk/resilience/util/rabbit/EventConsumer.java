package com.jbk.resilience.util.rabbit;

import com.jbk.resilience.interfaces.ICallbackEvent;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class EventConsumer extends DefaultConsumer {

    private ICallbackEvent callback;

    private String handlerName;


    public EventConsumer(ICallbackEvent callback, String handlerName, Channel channel) {
        super(channel);
        this.callback = callback;
        this.handlerName = handlerName;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        Runnable task = new CallbackTask(envelope.getDeliveryTag(), getChannel(), body, 2, callback, handlerName);
        task.run();
    }
}
