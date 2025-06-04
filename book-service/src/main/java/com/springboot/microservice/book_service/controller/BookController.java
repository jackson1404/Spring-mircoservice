/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.springboot.microservice.book_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BookController Class.
 * <p>
 * </p>
 *
 * @author
 */

@RestController
@RequestMapping("/books")
public class BookController {

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello from book service";
    }

}
