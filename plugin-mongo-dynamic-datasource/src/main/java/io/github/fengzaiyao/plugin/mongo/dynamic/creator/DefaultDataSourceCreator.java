package io.github.fengzaiyao.plugin.mongo.dynamic.creator;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Collections;

public class DefaultDataSourceCreator implements DataSourceCreator {

    private ApplicationContext applicationContext;

    public DefaultDataSourceCreator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public MongoTemplate createDataSource(MongoProperties properties) {
        // 1.创建 MongoClient
        MongoClient client = generateMongoClient(properties);
        // 2.创建 MongoTemplate
        return generateMongoTemplate(client, properties);
    }

    /**
     * 创建 MongoClient {@see MongoAutoConfiguration#mongo}
     */
    private MongoClient generateMongoClient(MongoProperties properties) {
        ObjectProvider<MongoClientOptions> options = applicationContext.getBeanProvider(MongoClientOptions.class);
        return new MongoClientFactory(properties, applicationContext.getEnvironment()).createMongoClient(options.getIfAvailable());
    }

    /**
     * 创建 MongoTemplate {@see MongoDbFactoryConfiguration#mongoDbFactory} {@see MongoDbFactoryDependentConfiguration#mongoTemplate}
     */
    private MongoTemplate generateMongoTemplate(MongoClient client, MongoProperties properties) {
        SimpleMongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(client, properties.getMongoClientDatabase());
        return new MongoTemplate(mongoDbFactory, generateMongoConverter(mongoDbFactory, properties));
    }

    /**
     * 创建 MongoConverter {@see MongoDbFactoryDependentConfiguration#mappingMongoConverter}
     */
    private MongoConverter generateMongoConverter(MongoDbFactory mongoDbFactory, MongoProperties properties) {
        MongoCustomConversions customConversions = generateMongoConversions();
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MongoMappingContext mappingContext = generateMongoMappingContext(customConversions, properties);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, mappingContext);
        mappingConverter.setCustomConversions(customConversions);
        return mappingConverter;
    }

    /**
     * 创建 MongoCustomConversions {@see MongoDataConfiguration#mongoCustomConversions}
     */
    private MongoCustomConversions generateMongoConversions() {
        return new MongoCustomConversions(Collections.emptyList());
    }

    /**
     * 创建 MongoMappingContext {@see MongoDataConfiguration#mongoMappingContext}
     */
    private MongoMappingContext generateMongoMappingContext(MongoCustomConversions conversions, MongoProperties properties) {
        try {
            PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
            MongoMappingContext context = new MongoMappingContext();
            mapper.from(properties.isAutoIndexCreation()).to(context::setAutoIndexCreation);
            context.setInitialEntitySet(new EntityScanner(applicationContext).scan(Document.class, Persistent.class));
            Class<?> strategyClass = properties.getFieldNamingStrategy();
            if (strategyClass != null) {
                context.setFieldNamingStrategy((FieldNamingStrategy) BeanUtils.instantiateClass(strategyClass));
            }
            context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
            return context;
        } catch (Exception ex) {
            throw new IllegalStateException("mongoTemplate scan @Document and @Persistent not found class");
        }
    }
}
