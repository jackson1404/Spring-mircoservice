/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.springboot.microservice.user_service.controller;

import com.springboot.microservice.user_service.service.TestingCircuitService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestingCircuitController Class.
 * <p>
 * </p>
 *
 * @author
 */
@RestController
@RequestMapping("/resilience-testing")
public class TestingCircuitController {

    @Autowired
    private TestingCircuitService testingCircuitService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @GetMapping("/test")
    public String testCall() {
        return testingCircuitService.callUnstableService();
    }

    @GetMapping("/state")
    public String getCircuitState() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("testService");
        return "Circuit State: " + circuitBreaker.getState();
    }

    @GetMapping("/reset")
    public String resetCircuit() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("testService");
        circuitBreaker.reset();
        return "Circuit Breaker Reset to CLOSED state";
    }

    @GetMapping("/metrics")
    public String getMetrics() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("testService");
        return String.format(
                "State: %s | Failures: %d | Success: %d | NotPermitted: %d",
                circuitBreaker.getState(),
                circuitBreaker.getMetrics().getNumberOfFailedCalls(),
                circuitBreaker.getMetrics().getNumberOfSuccessfulCalls(),
                circuitBreaker.getMetrics().getNumberOfNotPermittedCalls()
        );
    }
}