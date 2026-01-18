package ru.menshevva.demoapp.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.function.Function;

@Configuration(proxyBeanMethods = false)
public class SecondDataSourceConfiguration {

    @Bean(defaultCandidate = false)
    @ConfigurationProperties("second.datasource")
    public DataSourceProperties secondDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(defaultCandidate = false)
    @ConfigurationProperties("second.datasource.configuration")
    public HikariDataSource secondDataSource(
            @Qualifier("secondDataSourceProperties") DataSourceProperties secondDataSourceProperties) {
        return secondDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(defaultCandidate = false)
    @ConfigurationProperties("app.jpa")
    public JpaProperties secondJpaProperties() {
        return new JpaProperties();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean secondEntityManagerFactory(@Qualifier("secondDataSource") DataSource dataSource,
                                                                             @Qualifier("secondJpaProperties") JpaProperties jpaProperties) {
        JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        Function<DataSource, Map<String, ?>> jpaPropertiesFactory = ds -> jpaProperties.getProperties();
        EntityManagerFactoryBuilder builder =  new EntityManagerFactoryBuilder(jpaVendorAdapter, jpaPropertiesFactory, null);
        return builder.dataSource(dataSource)
                .packages("ru.menshevva.demoapp.entities.second")
                .persistenceUnit("second").build();
    }

    @Bean
    public PlatformTransactionManager secondaryTransactionManager(@Qualifier("secondEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }


}

