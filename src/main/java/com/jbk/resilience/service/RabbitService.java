package com.jbk.resilience.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbk.resilience.config.RabbitConfiguration;
import com.jbk.resilience.interfaces.ICallbackEvent;
import com.jbk.resilience.interfaces.IEvent;
import com.jbk.resilience.util.rabbit.EventConsumer;
import com.jbk.resilience.util.rabbit.EventPayload;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RabbitService {

    public static final String DELAY_HEADER = "x-delay";
    private final String EXCHANGE_TYPE = "topic";
    private final String EXCHANGE_TYPE_DELAY = "x-delayed-message";
    private final Boolean DURABLE = Boolean.TRUE;
    private final Boolean AUTO_DELETE = Boolean.TRUE;
    private final Boolean EXCLUSIVE = Boolean.FALSE;
    @Autowired
    private RabbitConfiguration rabbitConfiguration;
    private Connection connection;

    public Connection getRabbitConnection() {

        try {

            if (this.connection != null) {
                return this.connection;
            }

            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(rabbitConfiguration.getUser());
            factory.setPassword(rabbitConfiguration.getPassword());
            factory.setHost(rabbitConfiguration.getVhost());

            return this.connection = factory.newConnection();
        } catch (IOException e) {
            log.error("Error to create connection");
            e.printStackTrace();
        } catch (TimeoutException e) {
            log.error("Errot to create connection");
            e.printStackTrace();
        }

        return null;
    }


    public Channel createChannel() {
        try {
            return this.getRabbitConnection().createChannel();
        } catch (IOException e) {
            log.error("Error to create channel");
            e.printStackTrace();
        }

        return null;
    }

    public void declareExchange(String exchangeName) {

        Channel channel = null;
        try {
            channel = this.getRabbitConnection().createChannel();
            channel.exchangeDeclare(exchangeName, EXCHANGE_TYPE);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public Boolean closeChannel(Channel channel) {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    public void registerQueue(String eventName, String handlerName, ICallbackEvent callback, Integer qtdConsumers) {

        Channel channel = null;

        try {
            channel = this.createChannel();

            String routingKeyEvent = eventName;
            String routingKeyHandler = eventName + "." + handlerName.replaceAll("\\.", "_");

            String queueName = String.join(".", eventName, handlerName);

            this.declareExchange(eventName);
            //channel.exchangeDeclare(eventName, EXCHANGE_TYPE, DURABLE, false, null);
            channel.queueDeclare(queueName, DURABLE, EXCLUSIVE, AUTO_DELETE, new HashMap<>());
            channel.queueBind(queueName, eventName, routingKeyEvent);
            channel.queueBind(queueName, eventName, routingKeyHandler);


            Map<String, Object> args = new HashMap<>();
            args.put("x-delayed-type", EXCHANGE_TYPE);
            channel.exchangeDeclare(eventName + ".delay", "x-delayed-message", DURABLE, false, args);
            channel.queueDeclare(queueName + ".delay", DURABLE, false, false, null);
            channel.queueBind(queueName + ".delay", eventName + ".delay", routingKeyEvent);
            channel.queueBind(queueName + ".delay", eventName + ".delay", routingKeyHandler);

            for (int i = 0; i < qtdConsumers; i++) {
                if (channel == null) {
                    channel = connection.createChannel();
                    channel.basicQos(1);
                }

                Consumer consumer = new EventConsumer(callback, handlerName, channel);

                channel.basicConsume(queueName, false, consumer);
                channel.basicConsume(queueName + ".delay", false, consumer);
                channel = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMessage(String eventName, IEvent event, String handlerName) {

        Channel channel = null;

        try {

            channel = this.createChannel();

            channel.exchangeDeclare(eventName, this.EXCHANGE_TYPE);

            ObjectMapper mapper = new ObjectMapper();
            String message = mapper.writeValueAsString(new EventPayload(event, handlerName));
            message = StringUtils.stripAccents(message);

            log.info("Message sended. " + "Time: " + LocalDateTime.now());

            channel.basicPublish(eventName, eventName, null, message.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.closeChannel(channel);
        }
    }


    public void handleMessageDelayTime(String eventName, IEvent event, String handlerName, Integer delayInMilliSeconds) {

        Channel channel = null;

        final AMQP.BasicProperties properties = getBasicProperties(delayInMilliSeconds);

        String exchangeName = eventName;
        String routingKey = eventName;
        String exchangeType = "topic";

        if (handlerName != null) {
            routingKey = routingKey + "." + handlerName.replaceAll("\\.", "_");
        }

        Map<String, Object> args = null;
        if (delayInMilliSeconds != null && delayInMilliSeconds.intValue() > 0) {
            exchangeName = eventName + ".delay";
            exchangeType = EXCHANGE_TYPE_DELAY;
            args = new HashMap<>();
            args.put("x-delayed-type", "topic");

        }

        try {

            channel = this.createChannel();

            channel.exchangeDeclare(exchangeName, exchangeType, DURABLE, false, args);
            ObjectMapper mapper = new ObjectMapper();

            String message = mapper.writeValueAsString(new EventPayload(event, handlerName));
            message = StringUtils.stripAccents(message);

            log.info("Sending message with delay time -> ".concat(String.valueOf(delayInMilliSeconds)).concat("Time: ").concat(LocalDateTime.now().toString()));

            channel.basicPublish(exchangeName, routingKey, properties, message.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.closeChannel(channel);
        }


    }

    private AMQP.BasicProperties getBasicProperties(Integer timeOutMilisecounds) {
        if (timeOutMilisecounds != null && timeOutMilisecounds > 0) {
            Map<String, Object> headers = new HashMap<>();
            headers.put(DELAY_HEADER, timeOutMilisecounds);
            AMQP.BasicProperties.Builder propsBuilder = new AMQP.BasicProperties.Builder();
            propsBuilder.headers(headers);
            final AMQP.BasicProperties build = propsBuilder.build();
            return build;
        }
        return null;
    }

}
