package com.jbk.resilience.command;

import com.jbk.resilience.service.CircuitBreakService;
import org.springframework.stereotype.Component;

@Component
public class DoSomethingCommand extends CircuitBreakService {
}
