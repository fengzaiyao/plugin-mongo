package io.github.fengzaiyao.plugin.mongo.dynamic.processor;

import io.github.fengzaiyao.plugin.mongo.dynamic.model.DSFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class DataSourceBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private final Set<String> sources;

    public DataSourceBeanPostProcessor(String... sources) {
        this(Arrays.asList(sources));
    }

    public DataSourceBeanPostProcessor(Collection<String> sources) {
        this(new LinkedHashSet<>(sources));
    }

    public DataSourceBeanPostProcessor(Set<String> packagesToScan) {
        this.sources = packagesToScan;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (!CollectionUtils.isEmpty(sources)) {
            for (String source : sources) {
                // 注册Bean, BeanName == source
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(DSFactoryBean.class);
                builder.addConstructorArgValue(source);
                registry.registerBeanDefinition(source, builder.getBeanDefinition());
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
