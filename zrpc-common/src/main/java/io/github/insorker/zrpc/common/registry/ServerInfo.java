package io.github.insorker.zrpc.common.registry;

import com.alibaba.fastjson2.JSON;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ServerInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -2197578582408622414L;

    private String host;
    private int port;
    private List<ServiceInfo> serviceInfoList;

    public ServerInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public byte[] toJSONBytes() {
        return JSON.toJSONBytes(this);
    }

    public static ServerInfo parseObject(byte[] bytes) {
        return JSON.parseObject(bytes, ServerInfo.class);
    }

    public void addService(ServiceInfo serviceInfo) {
        serviceInfoList.add(serviceInfo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerInfo that = (ServerInfo) o;
        return port == that.port && Objects.equals(host, that.host) && Objects.equals(serviceInfoList, that.serviceInfoList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", services=" + serviceInfoList +
                '}';
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

    public List<ServiceInfo> getServiceInfoList() {
        return serviceInfoList;
    }

    public void setServiceInfoList(List<ServiceInfo> serviceInfoList) {
        this.serviceInfoList = serviceInfoList;
    }
}
