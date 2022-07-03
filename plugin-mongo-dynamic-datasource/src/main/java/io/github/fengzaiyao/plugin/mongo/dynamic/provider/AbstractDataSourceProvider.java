package io.github.fengzaiyao.plugin.mongo.dynamic.provider;

import io.github.fengzaiyao.plugin.mongo.dynamic.creator.DataSourceCreator;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractDataSourceProvider implements SmartInitializingSingleton, DataSourceProvider {

    private Map<String, MongoTemplate> sourceMap = new ConcurrentHashMap<>();

    @Autowired
    private DataSourceCreator sourceCreator;

    @Override
    public MongoTemplate getDataSources(String source) {
        return sourceMap.get(source);
    }

    @Override
    public Map<String, MongoTemplate> loadDataSources() {
        Map<String, MongoProperties> propertiesMap = loadDSProperties();
        for (Map.Entry<String, MongoProperties> item : propertiesMap.entrySet()) {
            sourceMap.put(item.getKey(), sourceCreator.createDataSource(item.getValue()));
        }
        return sourceMap;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // 主动调用创建数据源
        loadDataSources();
    }

    protected abstract Map<String, MongoProperties> loadDSProperties();
}
