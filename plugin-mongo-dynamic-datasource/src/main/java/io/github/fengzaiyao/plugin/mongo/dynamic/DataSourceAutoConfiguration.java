package io.github.fengzaiyao.plugin.mongo.dynamic;

import io.github.fengzaiyao.plugin.mongo.dynamic.annotation.DataSource;
import io.github.fengzaiyao.plugin.mongo.dynamic.annotation.DataSourceScanRegistrar;
import io.github.fengzaiyao.plugin.mongo.dynamic.aop.DataSourceAdvisor;
import io.github.fengzaiyao.plugin.mongo.dynamic.aop.DataSourceInterceptor;
import io.github.fengzaiyao.plugin.mongo.dynamic.config.DynamicDSProperties;
import io.github.fengzaiyao.plugin.mongo.dynamic.creator.DataSourceCreator;
import io.github.fengzaiyao.plugin.mongo.dynamic.creator.DefaultDataSourceCreator;
import io.github.fengzaiyao.plugin.mongo.dynamic.provider.DataSourceProvider;
import io.github.fengzaiyao.plugin.mongo.dynamic.provider.DefaultDataSourceProvider;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

@Configuration
@EnableConfigurationProperties(DynamicDSProperties.class)
@Import(DataSourceScanRegistrar.class)
public class DataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DataSourceCreator.class)
    public DataSourceCreator mongoPluginDefaultDataSourceCreator(ApplicationContext applicationContext) {
        return new DefaultDataSourceCreator(applicationContext);
    }

    @Bean
    public DataSourceProvider mongoPluginInnerDataSourceProvider(DataSourceCreator sourceCreator, DynamicDSProperties properties) {
        return new DefaultDataSourceProvider(sourceCreator, properties.getDatasource());
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor mongoPluginInnerDataSourceAdvisor(DataSourceProvider sourceProvider) {
        DataSourceInterceptor interceptor = new DataSourceInterceptor(sourceProvider);
        DataSourceAdvisor advisor = new DataSourceAdvisor(interceptor, DataSource.class);
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return advisor;
    }
}
