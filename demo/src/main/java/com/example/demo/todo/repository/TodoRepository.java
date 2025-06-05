/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.example.demo.todo.repository;

import com.example.demo.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * TodoRepository Class.
 * <p>
 * </p>
 *
 * @author
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
