package io.github.fengzaiyao.plugin.mongo.test.creator;

import io.github.fengzaiyao.plugin.mongo.dynamic.creator.DataSourceCreator;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MyDSCreator implements DataSourceCreator {
    @Override
    public MongoTemplate createDataSource(MongoProperties properties) {
        // 传递给对应数据源的 MongoProperties 文件了,可根据参数自定义创建过程
        // 返回 null 的会抛出异常, spring 会检查的
        return null;
    }
}
