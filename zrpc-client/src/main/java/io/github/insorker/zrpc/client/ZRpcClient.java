package io.github.insorker.zrpc.client;

import io.github.insorker.zrpc.client.client.NettyClient;
import io.github.insorker.zrpc.common.annotation.ZRpcReference;
import io.github.insorker.zrpc.common.exceptions.ZRpcException;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;

public class ZRpcClient extends NettyClient implements ApplicationContextAware, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(ZRpcClient.class);
    private static volatile ZRpcClient instance;

    private ZRpcClient(String registryAddress) {
        super(registryAddress);
    }

    public static ZRpcClient newInstance(String registryAddress) {
        if (instance == null) {
            synchronized (ZRpcClient.class) {
                if (instance == null) {
                    instance = new ZRpcClient(registryAddress);
                }
            }
        }
        return instance;
    }

    public static ZRpcClient getInstance() {
        if (instance == null) {
            throw new ZRpcException("ZRpcClient has not been built");
        }
        return instance;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
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
    public void destroy() {
        this.stop();
    }
}
