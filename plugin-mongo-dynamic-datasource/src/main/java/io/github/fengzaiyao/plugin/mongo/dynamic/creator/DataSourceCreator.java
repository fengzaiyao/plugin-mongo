package io.github.fengzaiyao.plugin.mongo.dynamic.creator;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.core.MongoTemplate;

public interface DataSourceCreator {

    MongoTemplate createDataSource(MongoProperties properties);
}
