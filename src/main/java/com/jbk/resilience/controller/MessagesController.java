package com.jbk.resilience.controller;

import com.jbk.resilience.service.message.PublishMessagesService;
import com.jbk.resilience.service.message.ReceiveMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessagesController {

    private final PublishMessagesService publishMessagesService;
    private final ReceiveMessagesService receiveMessagesService;

    @Autowired
    public MessagesController(PublishMessagesService publishMessagesService,
                              ReceiveMessagesService receiveMessagesService) {
        this.publishMessagesService = publishMessagesService;
        this.receiveMessagesService = receiveMessagesService;
    }

    @PostMapping
    @RequestMapping("/sendMessage")
    public ResponseEntity sengMessage(@RequestBody String message) {

        this.publishMessagesService.sendMessage(message);

        return ResponseEntity.ok("Sended message to be processed");
    }

    @PostMapping
    @RequestMapping("/sendMessage-delayed")
    public ResponseEntity sengMessageDelayed(@RequestBody String message) {


        for (int i = 0; i < 1000; i++) {
            this.publishMessagesService.sendMessageDelayed(message + "numero " + i);
        }


        return ResponseEntity.ok("Sended message to be processed");
    }

}
