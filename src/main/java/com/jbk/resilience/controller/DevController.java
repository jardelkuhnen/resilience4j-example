package com.jbk.resilience.controller;

import com.jbk.resilience.service.ServiceSomething;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class DevController {

    @Autowired
    private ServiceSomething serviceSomething;

    @GetMapping
    public ResponseEntity doSomething() {
        return ResponseEntity.ok(this.serviceSomething.doSomething());
    }

}
