package com.payment.processing.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Component
@ConfigurationProperties(prefix = "app")
public class DataSourceProperties {

    private final Map<String, DataSource> datasources = new LinkedHashMap<>();
    private final Map<Integer, DataSource> dbNumberDataSource = new HashMap<>();

    public int getDatabaseCount() {
        return dbNumberDataSource.values().size();
    }

    public void setDatasources(Map<String, Map<String, String>> datasources) {
        datasources.forEach((key, value) -> this.datasources.put(key, convert(value)));
        int i = 0;
        for (DataSource dataSource : this.datasources.values()) {
            dbNumberDataSource.put(i++, dataSource);
        }
    }

    public DataSource convert(Map<String, String> source) {
        return DataSourceBuilder.create()
                .url(source.get("jdbc-url"))
                .driverClassName(source.get("driver-class-name"))
                .username(source.get("username"))
                .password(source.get("password"))
                .build();
    }
}
