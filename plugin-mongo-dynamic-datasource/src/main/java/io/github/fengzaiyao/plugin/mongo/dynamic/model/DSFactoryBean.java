package io.github.fengzaiyao.plugin.mongo.dynamic.model;

import io.github.fengzaiyao.plugin.mongo.dynamic.provider.DataSourceProvider;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class DSFactoryBean implements FactoryBean<MongoTemplate> {

    @Autowired
    private DataSourceProvider sourceProvider;

    private final String source;

    public DSFactoryBean(String source) {
        this.source = source;
    }

    @Override
    public MongoTemplate getObject() throws Exception {
        return sourceProvider.getDataSources(source);
    }

    @Override
    public Class<?> getObjectType() {
        return MongoTemplate.class;
    }
}