/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.springcloud.demo_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * StartController Class.
 * <p>
 * </p>
 *
 * @author
 */

@RestController
@RefreshScope
public class StartController {

    @Value("${app.message}")
    public String appMessage;

    @GetMapping("/")
    public String returnMsg(){
        return "Config server msg from Demo service is " + appMessage;
    }
}
