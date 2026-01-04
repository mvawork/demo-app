package ru.menshevva.demoapp.config;

import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
public class MainJpaConfiguration {

    private final EntityManagerFactoryBuilder entityManagerFactoryBuilder;

    public MainJpaConfiguration(EntityManagerFactoryBuilder entityManagerFactoryBuilder) {
        this.entityManagerFactoryBuilder = entityManagerFactoryBuilder;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            JpaProperties jpaProperties) {
        return entityManagerFactoryBuilder
                .dataSource(dataSource)
                .packages("ru.menshevva.demoapp.entities.main") // Только основные сущности
                .persistenceUnit("main")
                .build();
    }

}
