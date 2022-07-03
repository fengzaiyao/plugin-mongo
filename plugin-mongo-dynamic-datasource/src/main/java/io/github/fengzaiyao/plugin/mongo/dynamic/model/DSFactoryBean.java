package io.github.fengzaiyao.plugin.mongo.dynamic.model;

import io.github.fengzaiyao.plugin.mongo.dynamic.provider.DataSourceProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Objects;

public class DSFactoryBean implements FactoryBean<MongoTemplate>, BeanFactoryAware {

    private DataSourceProvider sourceProvider;

    private BeanFactory beanFactory;

    private final String source;

    public DSFactoryBean(String source) {
        this.source = source;
    }

    @Override
    public MongoTemplate getObject() throws Exception {
        loadSourceProviderIfNecessary();
        return sourceProvider.getDataSource(this.source);
    }

    @Override
    public Class<?> getObjectType() {
        return MongoTemplate.class;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private void loadSourceProviderIfNecessary() {
        if (Objects.isNull(sourceProvider)) {
            this.sourceProvider = beanFactory.getBean(DataSourceProvider.class);
        }
    }
}