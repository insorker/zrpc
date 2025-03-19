package io.github.insorker.zrpc.server;

import io.github.insorker.zrpc.common.annotation.ZRpcService;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import io.github.insorker.zrpc.server.server.NettyServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class ZRpcServer extends NettyServer implements ApplicationContextAware, InitializingBean, DisposableBean {

    public ZRpcServer(String host, int port, String registryAddress) {
        super(host, port, registryAddress);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(ZRpcService.class);

        serviceBeanMap.values().forEach(serviceBean -> {
            ZRpcService annotation = serviceBean.getClass().getAnnotation(ZRpcService.class);
            addService(new ServiceInfo(annotation.value()), serviceBean);
        });
    }

    @Override
    public void afterPropertiesSet() {
        start();
    }

    @Override
    public void destroy() {
        stop();
    }
}
