package io.github.fengzaiyao.plugin.mongo.dynamic.support;

import io.github.fengzaiyao.plugin.mongo.dynamic.annotation.DataSource;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ClassUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Proxy;

public class DataSourceClassResolve {

    public String computeDatasource(Object targetObject) {
        Class<?> targetClass = targetObject.getClass();
        // 可能是cglib生成的子类,如果是则返回原始类,否则返回targetObject的class
        Class<?> userClass = ClassUtils.getUserClass(targetClass);
        // 1.直接在当前类中寻找注解
        String sourceAttribute = findDataSourceAttribute(userClass);
        if (sourceAttribute != null) {
            return sourceAttribute;
        }
        // 2.从该类实现的接口中寻找,如果实现多个接口,接口中找到多个注解,只取第一个找到的
        for (Class<?> interfaceClazz : ClassUtils.getAllInterfacesForClassAsSet(userClass)) {
            sourceAttribute = findDataSourceAttribute(interfaceClazz);
            if (sourceAttribute != null) {
                return sourceAttribute;
            }
        }
        // 3.如果没有被代理,从父类一级级往上找
        if (!Proxy.isProxyClass(targetClass)) {
            Class<?> currentClass = targetClass;
            while (currentClass != Object.class) {
                String datasourceAttr = findDataSourceAttribute(currentClass);
                if (datasourceAttr != null) {
                    return datasourceAttr;
                }
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    private String findDataSourceAttribute(AnnotatedElement ae) {
        AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ae, DataSource.class);
        return attributes == null ? null : attributes.getString("value");
    }
}
