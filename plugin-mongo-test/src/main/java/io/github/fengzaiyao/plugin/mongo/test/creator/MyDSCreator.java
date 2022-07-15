package io.github.fengzaiyao.plugin.mongo.test.creator;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import io.github.fengzaiyao.plugin.mongo.dynamic.creator.DataSourceCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.stereotype.Component;

@Component
public class MyDSCreator implements DataSourceCreator, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public MongoTemplate createDataSource(MongoProperties properties) {
        ObjectProvider<MongoClientOptions> options = applicationContext.getBeanProvider(MongoClientOptions.class);
        MongoClient client = new MongoClientFactory(properties, applicationContext.getEnvironment()).createMongoClient(options.getIfAvailable());
        SimpleMongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(client, properties.getMongoClientDatabase());
        return new MongoTemplate(mongoDbFactory);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
