package io.github.fengzaiyao.plugin.mongo.dynamic.provider;

import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;

public interface DataSourceProvider {

    Map<String, MongoTemplate> loadDataSources();

    MongoTemplate getDataSources(String source);
}
