package io.github.fengzaiyao.plugin.mongo;

import io.github.fengzaiyao.plugin.mongo.service.IBrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;

@SpringBootTest
public class MongoPluginTestApplication {

    @Autowired
    private IBrandService brandService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Resource(name = "datasource1")
    private MongoTemplate mongoDB1;

    @Resource(name = "datasource2")
    private MongoTemplate mongoDB2;

    @Test
    public void TestTemplateEQ() {
        System.out.println(brandService.getMongoTemplate() == mongoDB1);
        System.out.println(mongoDB1);
        System.out.println(mongoDB2);
        System.out.println(mongoTemplate);
        System.out.println(brandService.getMongoTemplate());
    }
}
