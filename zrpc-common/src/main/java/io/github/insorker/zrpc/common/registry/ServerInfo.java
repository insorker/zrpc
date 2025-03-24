package io.github.insorker.zrpc.common.registry;

import com.alibaba.fastjson2.JSON;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -2197578582408622414L;
    private SocketInfo socketInfo;
    private List<ServiceInfo> serviceInfoList;

    public ServerInfo(String host, int port) {
        socketInfo = new SocketInfo(host, port);
        serviceInfoList = new ArrayList<>();
    }

    public byte[] toJSONBytes() {
        return JSON.toJSONBytes(this);
    }

    public static ServerInfo parseObject(byte[] bytes) {
        return JSON.parseObject(bytes, ServerInfo.class);
    }

    public String getPath() {
        return String.valueOf(hashCode());
    }

    public void addService(ServiceInfo serviceInfo) {
        serviceInfoList.add(serviceInfo);
    }

    public void removeService(ServiceInfo serviceInfo) {
        serviceInfoList.remove(serviceInfo);
    }

    public String getHost() {
        return socketInfo.getHost();
    }

    public int getPort() {
        return socketInfo.getPort();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerInfo that = (ServerInfo) o;
        return Objects.equals(socketInfo, that.socketInfo) && Objects.equals(serviceInfoList, that.serviceInfoList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socketInfo, serviceInfoList);
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "socketInfo=" + socketInfo +
                ", serviceInfoList=" + serviceInfoList +
                '}';
    }

    public SocketInfo getSocketInfo() {
        return socketInfo;
    }

    public void setSocketInfo(SocketInfo socketInfo) {
        this.socketInfo = socketInfo;
    }

    public List<ServiceInfo> getServiceInfoList() {
        return serviceInfoList;
    }

    public void setServiceInfoList(List<ServiceInfo> serviceInfoList) {
        this.serviceInfoList = serviceInfoList;
    }
}
