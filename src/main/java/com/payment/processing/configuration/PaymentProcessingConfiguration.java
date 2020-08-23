package com.payment.processing.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@AllArgsConstructor
public class PaymentProcessingConfiguration {

    private final DataSourceProperties dataSourceProperties;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    @Primary
    public DataSource firstDataSource() {
        return dataSourceProperties.getDatasources().values().stream().findFirst().get();
    }
}
