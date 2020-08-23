package com.payment.processing.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Component
@ConfigurationProperties(prefix = "app")
public class DataSourceProperties {

    private Map<String, DataSource> datasources = new LinkedHashMap<>();
    private Map<Integer, JdbcTemplate> dbNumberJdbcTemplate = new HashMap<>();

//    public Map<Object, Object> getDatasources() {
//        return datasources;
//    }

    public int getDatabaseCount() {
        return dbNumberJdbcTemplate.values().size();
    }

    public void setDatasources(Map<String, Map<String, String>> datasources) {
        datasources.forEach((key, value) -> this.datasources.put(key, convert(value)));
//        for (int i = 0; i < this.getDatasources().values().size(); i++) {
//            dbNumberJdbcTemplate.put(i, new JdbcTemplate(this.getDatasources().values().))
//        }
//        IntStream.range(0, this.datasources.values().size())
//                .boxed().collect(Collectors.toMap(Function.identity(), this.datasources.values()::get))
        int i = 0;
        for (DataSource dataSource : this.datasources.values()) {
            dbNumberJdbcTemplate.put(i++, new JdbcTemplate(dataSource));
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

//    @PostConstruct
//    public void init() {
//        final int size = datasources.size();
//    }
}
