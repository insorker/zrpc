package io.github.insorker.zrpc.common.registry;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ServiceInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1180582238681877681L;

    private String serviceName;

    public ServiceInfo(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceInfo that = (ServiceInfo) o;
        return Objects.equals(serviceName, that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName);
    }

    @Override
    public String toString() {
        return "ServiceInfo{" +
                "serviceName='" + serviceName + '\'' +
                '}';
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
