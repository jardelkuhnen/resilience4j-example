package com.jbk.resilience.service;

import org.springframework.stereotype.Service;

@Service
public class CommonsService {

    protected static final String FALLBACK_METHOD = "executeCallback";

}
