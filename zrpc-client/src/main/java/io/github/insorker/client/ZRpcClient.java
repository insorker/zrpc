package io.github.insorker.client;

import io.github.insorker.client.client.NettyClient;
import io.github.insorker.zrpc.common.annotation.ZRpcReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.util.Map;

public class ZRpcClient extends NettyClient implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ZRpcClient.class);

    public ZRpcClient(String registryAddress) {
        super(registryAddress);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> referenceBeanMap = applicationContext.getBeansWithAnnotation(ZRpcReference.class);

        referenceBeanMap.keySet().forEach(beanName -> {
            Object bean = applicationContext.getBean(beanName);
            Field[] fields = bean.getClass().getDeclaredFields();

            for (Field field : fields) {
                ZRpcReference annotation = field.getAnnotation(ZRpcReference.class);
                if (annotation != null) {
                    field.setAccessible(true);
//                    field.set(bean, )
                }
            }
        });
    }
}
