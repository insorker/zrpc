package io.github.insorker.zrpc.client;

import io.github.insorker.zrpc.common.annotation.ZRpcReference;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;

public interface SpringClient extends ZRpcClient, ApplicationContextAware, DisposableBean {

    Logger logger = LoggerFactory.getLogger(SpringClient.class);

    @Override
    default void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Field[] fields = bean.getClass().getDeclaredFields();

            for (Field field : fields) {
                ZRpcReference annotation = field.getAnnotation(ZRpcReference.class);
                if (annotation != null) {
                    field.setAccessible(true);
                    try {
                        field.set(bean, createService(new ServiceInfo(annotation.value()), field.getType()));
                    } catch (IllegalAccessException e) {
                        logger.error(e.toString());
                    }
                }
            }
        }
    }

    @Override
    default void destroy() {
        this.close();
    }
}
