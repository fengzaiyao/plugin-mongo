# MongoPlugin的功能
1. 支持类似于MyBatis-plus的BaseService的基本操作
2. 多数据源自动生成
3. 切换数据源

# 支持基本的操作
1.先引入下面的依赖
```xml
<dependency>
  <groupId>io.github.fengzaiyao</groupId>
  <artifactId>plugin-mongo-core</artifactId>
  <version>1.0.0</version>
</dependency>

<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-mongodb</artifactId>
</dependency>
```
2.配置文件配置
```properties
# springboot 怎么配置mongo连接你就怎么配置
spring.data.mongodb.uri=mongodb://localhost/admin
```
3.实体类
```java
// 记得继承 BaseEntity,里面拥有基本字段
@Document("brand")
public class Brand extends BaseEntity {
    // 下面是你自己的业务字段
    public String name;
    public Integer number;
}
```
4.业务service接口
```java
// 记得继承 IBaseService
public interface IBrandService extends IBaseService<Brand, String> {
}
```
5.业务service
```java
// 记得继承 BaseServiceImpl,里面拥有基本方法
@Service
public class BrandServiceImpl extends BaseServiceImpl<Brand, String> implements IBrandService {
}
```
6.直接使用即可(和mybatis-plus的操作基本一致)
```java
@SpringBootTest
public class MongoPluginTestApplication {

    @Autowired
    private IBrandService brandService;

    @Test
    public void TestTemplateBaseOperation() {
        Brand brand = brandService.findById("afs2r24r22r2");
        System.out.println(brand);
    }
}
```
# 多数据源配置 and 自动切换数据源

1.在前面引入依赖的基础上再引入下面这个依赖
```xml
<dependency>
  <groupId>io.github.fengzaiyao</groupId>
  <artifactId>plugin-mongo-dynamic-datasource</artifactId>
  <version>1.0.0</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

2.多数据源配置
```
# 配置文件这么写：
# 书写格式 spring.data.mongodb.plugin.dynamic.datasource.数据源名字.uri=mongodb://root:root@xx.xx.xx.xx:27017/admin
# 有多少条配置,配置多少个数据源，spring配置怎么写，你就怎么写
spring.data.mongodb.plugin.dynamic.datasource.dddd1111.uri=mongodb://root:root@xx.xx.xx.xx:27017/admin
spring.data.mongodb.plugin.dynamic.datasource.dddd2222.uri=mongodb://root:root@xx.xx.xx.xx:27018/admin
```
```java
// 获取配置的多数据源
@Resource(name = "dddd1111")
private MongoTemplate mongoDB1;
```

3.自动切换数据源
```java
// 例如还是以上面的业务service举例子，只需要添加 @DataSource("dddd1111") 既可指定数据源
// 如果你不写 @DataSource("dddd1111") 的话，默认使用 spring 提供的 mongoTemplate
@Service
@DataSource("dddd1111")
public class BrandServiceImpl extends BaseServiceImpl<Brand, String> implements IBrandService {
}
```

# 自定义MongoTemplate的创建
有时候，你想自定义MongoTemplate的创建过程，那么你只需要注入 DataSourceCreator 类既可。
```
@Component
public class MyDSCreator implements DataSourceCreator {
    @Override
    public MongoTemplate createDataSource(MongoProperties properties) {
        // 传递给对应数据源的 MongoProperties 文件了,可根据参数自定义创建过程
        // 返回 null 的会抛出异常, spring 会检查的
        return null;
    }
}
```