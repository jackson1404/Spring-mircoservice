/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.springcloud.config_server.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig Class.
 * <p>
 * </p>
 *
 * @author
 */
@Configuration
public class SecurityConfig {

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow health check and actuator endpoints without auth
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        // Secure all other endpoints
                        .anyRequest().authenticated()
                )
                .httpBasic()  // Use HTTP Basic authentication
                .and()
                .csrf(csrf -> csrf.disable()); // Disable CSRF for simplicity (optional)

        return http.build();
    }

    @Bean
    public UserDetailsService users(){
        UserDetails user = User.withDefaultPasswordEncoder()
                .username(username)
                .password(password)
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }


}
