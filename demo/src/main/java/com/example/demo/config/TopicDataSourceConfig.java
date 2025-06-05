package com.example.demo.config;

import com.example.demo.topic.entity.Topic;
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
        basePackages = "com.example.demo.topic.repository",
        entityManagerFactoryRef = "topicEntityManagerFactory",
        transactionManagerRef = "topicTransactionManager"
)
@EntityScan(basePackageClasses = Topic.class)
public class TopicDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.topics")
    public DataSourceProperties topicDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource topicDataSource() {
        return topicDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean topicEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(topicDataSource())
                .packages(Topic.class)
                .build();
    }

    @Bean
    public PlatformTransactionManager topicTransactionManager(
            final @Qualifier("topicEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(Objects.requireNonNull(factory.getObject()));
    }
}
