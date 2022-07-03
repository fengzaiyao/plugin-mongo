package io.github.fengzaiyao.plugin.mongo.dynamic.aop;

import io.github.fengzaiyao.plugin.mongo.dynamic.exception.NotFindDataSourceException;
import io.github.fengzaiyao.plugin.mongo.dynamic.provider.DataSourceProvider;
import io.github.fengzaiyao.plugin.mongo.dynamic.support.DataSourceClassResolve;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class DataSourceInterceptor implements MethodInterceptor {

    private final DataSourceClassResolve classResolve = new DataSourceClassResolve();

    private final DataSourceProvider sourceProvider;

    public DataSourceInterceptor(DataSourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object targetObject = invocation.getThis();
        String datasource = classResolve.computeDatasource(targetObject);
        // 标记 @DataSource 注解
        if (!StringUtils.isEmpty(datasource)) {
            Method method = targetObject.getClass().getMethod("setMongoTemplate", MongoTemplate.class);
            MongoTemplate replaceDS = sourceProvider.getDataSource(datasource);
            if (replaceDS == null) {
                throw new NotFindDataSourceException("No data source named " + datasource + " was found");
            }
            method.invoke(targetObject, replaceDS);
        }
        return invocation.proceed();
    }
}
