package ru.menshevva.demoapp.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
//@ConditionalOnProperty(prefix = "second.datasource", name = "enable", havingValue = "true")
public class SecondDataSourceConfiguration {

    @Qualifier("second")
    @Bean(defaultCandidate = false)
    @ConfigurationProperties("second.datasource")
    public DataSourceProperties secondDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Qualifier("second")
    @Bean(defaultCandidate = false)
    @ConfigurationProperties("second.datasource.configuration")
    public HikariDataSource secondDataSource(
            @Qualifier("secondDataSourceProperties") DataSourceProperties secondDataSourceProperties) {
        return secondDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

}

