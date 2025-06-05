package com.example.demo.config;

import com.example.demo.todo.entity.Todo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.demo.todo.repository",
        entityManagerFactoryRef = "todoEntityManagerFactory",
        transactionManagerRef = "todoTransactionManager"
)
@EntityScan(basePackageClasses = Todo.class)
public class TodoDataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.todos")
    public DataSourceProperties todoDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource todoDataSource() {
        return todoDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean todoEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(todoDataSource())
                .packages(Todo.class)
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager todoTransactionManager(
            final @Qualifier("todoEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(Objects.requireNonNull(factory.getObject()));
    }
}
