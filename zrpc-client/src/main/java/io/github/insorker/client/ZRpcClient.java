package io.github.insorker.client;

import io.github.insorker.client.client.NettyClient;
import io.github.insorker.zrpc.common.annotation.EnableZRpc;
import io.github.insorker.zrpc.common.annotation.ZRpcReference;
import io.github.insorker.zrpc.common.exceptions.ZRpcException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class ZRpcClient extends NettyClient implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ZRpcClient.class);
    private static volatile ZRpcClient instance;

    private ZRpcClient(String registryAddress) {
        super(registryAddress);
    }

    static void newInstance(String registryAddress) {
        if (instance == null) {
            synchronized (ZRpcClient.class) {
                if (instance == null) {
                    instance = new ZRpcClient(registryAddress);
                }
            }
        }
    }

    public static ZRpcClient getInstance() {
        if (instance == null) {
            throw new ZRpcException("ZRpcClient has not been built");
        }
        return instance;
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

@Aspect
class ZRpcClientEnable {

    @Before("@annotation(io.github.insorker.zrpc.common.annotation.EnableZRpc)")
    public void initZRpcClient(JoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法上的注解
        EnableZRpc annotation = method.getAnnotation(EnableZRpc.class);
        if (annotation != null) {
            String registryAddress = annotation.value();
            // TODO: check if value is valid
            ZRpcClient.newInstance(registryAddress);
        }
    }
}
