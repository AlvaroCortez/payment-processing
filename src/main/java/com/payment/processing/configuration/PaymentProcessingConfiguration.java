package com.payment.processing.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.data.transaction.ChainedTransactionManager;

import javax.sql.DataSource;

@Configuration
public class PaymentProcessingConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

//    @Bean
//    @Primary
//    @ConfigurationProperties("app.datasource.first")
//    public DataSourceProperties firstDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean
//    @Primary
//    @ConfigurationProperties("app.datasource.first.configuration")
//    public DataSource firstDataSource() {
//        return firstDataSourceProperties().initializeDataSourceBuilder().build();
//    }

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.first")
    public DataSource firstDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate firstJdbcTemplate() {
        return new JdbcTemplate(firstDataSource());
    }

    @Bean("firstTx")
    public PlatformTransactionManager firstPlatformTransactionManager() {
        return new DataSourceTransactionManager(firstDataSource());
    }

    @Bean
    @ConfigurationProperties("app.datasource.second")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate secondaryJdbcTemplate() {
        return new JdbcTemplate(secondaryDataSource());
    }

    @Bean("secondaryTx")
    public PlatformTransactionManager secondaryPlatformTransactionManager() {
        return new DataSourceTransactionManager(secondaryDataSource());
    }

    @Bean
    @Primary
    public ChainedTransactionManager transactionManager() {
        return new ChainedTransactionManager(firstPlatformTransactionManager(),
                secondaryPlatformTransactionManager(),
                thirdPlatformTransactionManager());
    }

    @Bean
    @ConfigurationProperties("app.datasource.third")
    public DataSource thirdDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate thirdJdbcTemplate() {
        return new JdbcTemplate(thirdDataSource());
    }

    @Bean("thirdTx")
    public PlatformTransactionManager thirdPlatformTransactionManager() {
        return new DataSourceTransactionManager(thirdDataSource());
    }
}
