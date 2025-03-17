package io.github.insorker.zrpc.common.registry;

import com.alibaba.fastjson2.JSON;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ServiceInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1180582238681877681L;

    private String host;
    private int port;
    private String serviceName;

    public ServiceInfo(String host, int port, String serviceName) {
        this.host = host;
        this.port = port;
        this.serviceName = serviceName;
    }

    public byte[] toJSONBytes() {
        return JSON.toJSONBytes(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceInfo that = (ServiceInfo) o;
        return port == that.port && Objects.equals(host, that.host) && Objects.equals(serviceName, that.serviceName);
    }

    @Override
    public String toString() {
        return "ServiceInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, serviceName);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
