/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.example.demo.topic.repository;

import com.example.demo.topic.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * TopicRepository Class.
 * <p>
 * </p>
 *
 * @author
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
}
