package io.github.fengzaiyao.plugin.mongo.dynamic.annotation;

import io.github.fengzaiyao.plugin.mongo.dynamic.config.DynamicDSProperties;
import io.github.fengzaiyao.plugin.mongo.dynamic.processor.DataSourceBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DataSourceScanRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 1.获取所有的数据源名称
        Set<String> sources = getDataSource();
        // 2.注册 DataSourceBeanPostProcessor
        registerDataSourceBeanPostProcessor(sources, registry);
    }

    private Set<String> getDataSource() {
        String sourcePre = DynamicDSProperties.PREFIX + "." + "datasource";
        Pattern pattern = Pattern.compile("^" + sourcePre + "\\." + "(.+)" + "\\." + "uri" + "$");
        MutablePropertySources propertySources = ((AbstractEnvironment) environment).getPropertySources();
        return StreamSupport.stream(propertySources.spliterator(), false)
                .filter(propertySource -> propertySource instanceof EnumerablePropertySource)
                .map(propertySource -> ((EnumerablePropertySource) propertySource).getPropertyNames())
                .flatMap(Arrays::stream)
                .filter(propertyName -> pattern.matcher(propertyName).find())
                .map(sourceString -> parseStringToSource(sourcePre, sourceString))
                .collect(Collectors.toSet());
    }

    private void registerDataSourceBeanPostProcessor(Set<String> sources, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(DataSourceBeanPostProcessor.class);
        builder.addConstructorArgValue(sources);
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
    }

    private String parseStringToSource(String sourcePre, String sourceString) {
        return sourceString.substring(sourcePre.length() + 1, sourceString.lastIndexOf("."));
    }
}