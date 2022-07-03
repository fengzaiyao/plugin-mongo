package io.github.fengzaiyao.plugin.mongo.dynamic.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@Setter
@Getter
@ConfigurationProperties(prefix = DynamicDSProperties.PREFIX)
public class DynamicDSProperties {

    public static final String PREFIX = "spring.data.mongodb.plugin.dynamic";

    private Map<String, MongoProperties> datasource = new LinkedHashMap<>();
}