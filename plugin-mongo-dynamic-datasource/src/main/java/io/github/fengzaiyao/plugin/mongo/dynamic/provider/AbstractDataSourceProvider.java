package io.github.fengzaiyao.plugin.mongo.dynamic.provider;

import io.github.fengzaiyao.plugin.mongo.dynamic.creator.DataSourceCreator;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractDataSourceProvider implements DataSourceProvider {

    private final Map<String, MongoTemplate> sourceMap;

    private final DataSourceCreator sourceCreator;

    public AbstractDataSourceProvider(DataSourceCreator sourceCreator) {
        if (Objects.isNull(sourceCreator)) {
            throw new IllegalArgumentException("DataSourceProvider construction parameters cannot be null");
        }
        this.sourceMap = new ConcurrentHashMap<>();
        this.sourceCreator = sourceCreator;
    }

    @Override
    public MongoTemplate getDataSource(String sourceName) {
        return getDataSources().get(sourceName);
    }

    @Override
    public Map<String, MongoTemplate> getDataSources() {
        return sourceMap.isEmpty() ? initDataSources() : sourceMap;
    }

    private Map<String, MongoTemplate> initDataSources() {
        Map<String, MongoProperties> propertiesMap = loadDataSourceProperties();
        if (propertiesMap != null && !propertiesMap.isEmpty()) {
            for (Map.Entry<String, MongoProperties> entry : propertiesMap.entrySet()) {
                synchronized (sourceCreator) {
                    MongoTemplate source = sourceCreator.createDataSource(entry.getValue());
                    if (!Objects.isNull(source)) {
                        sourceMap.put(entry.getKey(), source);
                    }
                }
            }
        }
        return sourceMap;
    }

    protected abstract Map<String, MongoProperties> loadDataSourceProperties();
}
