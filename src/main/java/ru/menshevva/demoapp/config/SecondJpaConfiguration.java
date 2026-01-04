package ru.menshevva.demoapp.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Configuration(proxyBeanMethods = false)
public class SecondJpaConfiguration {

    @Qualifier("second")
    @Bean(defaultCandidate = false)
    @ConfigurationProperties("app.jpa")
    public JpaProperties secondJpaProperties() {
        return new JpaProperties();
    }

    @Qualifier("second")
    @Bean(defaultCandidate = false)
    public LocalContainerEntityManagerFactoryBean secondEntityManagerFactory(@Qualifier("second") DataSource dataSource,
                                                                             @Qualifier("second") JpaProperties jpaProperties) {
        EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(jpaProperties);
        return builder.dataSource(dataSource)
                .packages("ru.menshevva.demoapp.entities.second")
                .persistenceUnit("second").build();
    }

    private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(JpaProperties jpaProperties) {
        JpaVendorAdapter jpaVendorAdapter = createJpaVendorAdapter(jpaProperties);
        Function<DataSource, Map<String, ?>> jpaPropertiesFactory = (dataSource) -> createJpaProperties(dataSource,
                jpaProperties.getProperties());
        return new EntityManagerFactoryBuilder(jpaVendorAdapter, jpaPropertiesFactory, null);
    }

    private JpaVendorAdapter createJpaVendorAdapter(JpaProperties jpaProperties) {
        // ... map JPA properties as needed
        return new HibernateJpaVendorAdapter();
    }

    private Map<String, ?> createJpaProperties(DataSource dataSource, Map<String, ?> existingProperties) {
        Map<String, ?> jpaProperties = new LinkedHashMap<>(existingProperties);
        // ... map JPA properties that require the DataSource (e.g. DDL flags)
        return jpaProperties;
    }


}
