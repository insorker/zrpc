package com.insorker.zrpc;

import com.insorker.zrpc.annotation.ZRpcService;
import com.insorker.zrpc.registry.ServiceRegistry;
import com.insorker.zrpc.server.NettyServer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class ZRpcServer extends NettyServer implements ApplicationContextAware {

    public ZRpcServer(String host, int port, String registryAddress) {
        super(host, port, new ServiceRegistry(registryAddress));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(ZRpcService.class);

        serviceBeanMap.values().forEach(serviceBean -> {
            ZRpcService annotation = serviceBean.getClass().getAnnotation(ZRpcService.class);
            addService(annotation.value().getName(), serviceBean);
        });
    }
}
