package io.github.fengzaiyao.plugin.mongo.dynamic.provider;

import io.github.fengzaiyao.plugin.mongo.dynamic.creator.DataSourceCreator;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;

import java.util.Map;

public class DefaultDataSourceProvider extends AbstractDataSourceProvider implements DataSourceProvider {

    private final Map<String, MongoProperties> propertiesMap;

    public DefaultDataSourceProvider(DataSourceCreator sourceCreator, Map<String, MongoProperties> propertiesMap) {
        super(sourceCreator);
        this.propertiesMap = propertiesMap;
    }

    @Override
    protected Map<String, MongoProperties> loadDataSourceProperties() {
        return propertiesMap;
    }
}
