package io.github.fengzaiyao.plugin.mongo.dynamic.provider;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceInnerProvider extends AbstractDataSourceProvider implements DataSourceProvider {

    private final Map<String, MongoProperties> propertiesMap = new ConcurrentHashMap<>();

    public DataSourceInnerProvider(Map<String, MongoProperties> propertiesMap) {
        this.propertiesMap.putAll(propertiesMap);
    }

    @Override
    protected Map<String, MongoProperties> loadDSProperties() {
        return this.propertiesMap;
    }
}
