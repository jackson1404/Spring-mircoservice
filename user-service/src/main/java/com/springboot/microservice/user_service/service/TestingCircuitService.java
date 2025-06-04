/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.springboot.microservice.user_service.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * TestingCircuitService Class.
 * <p>
 * </p>
 *
 * @author
 */
@Service
public class TestingCircuitService {

    private int counter = 0;

    @CircuitBreaker(name = "testService", fallbackMethod = "callFallBackTesting")
    public String callUnstableService(){
        counter++;
        System.out.println("➡️ Attempt #" + counter);

        // Simulate failure 4 out of 5 times
        if (counter % 5 != 0) {
            System.out.println("❌ Simulated failure");
            throw new RuntimeException("Service failed");
        }

        System.out.println("✅ Simulated success");
        return "✅ Service call succeeded!";
    }

    public String callFallBackTesting(Throwable t){
        System.out.println("⚠️ Fallback called due to: " + t.getMessage());
        return "❌ Fallback response (Service down)";
    }
}
