/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.springboot.microservice.user_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UserService Class.
 * <p>
 * </p>
 *
 * @author
 */
@Service
@FeignClient(name = "book-service")
public interface UserService {

    @GetMapping("/books/hello")
    public String getHelloFromBookService();

}
