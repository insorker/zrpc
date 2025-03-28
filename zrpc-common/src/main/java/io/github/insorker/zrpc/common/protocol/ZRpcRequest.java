package io.github.insorker.zrpc.common.protocol;

import io.github.insorker.zrpc.common.registry.ServiceInfo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class ZRpcRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -7079157869973225277L;
    private String id;
    private ServiceInfo serviceInfo;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    public ZRpcRequest() {
        this.id = UUID.randomUUID().toString();
    }

    public ZRpcRequest(ServiceInfo serviceInfo, String className, String methodName, Object[] args) {
        this.id = UUID.randomUUID().toString();
        this.serviceInfo = serviceInfo;
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = new Class[args.length];
        this.parameters = args;

        for (int i = 0; i < args.length; i++) {
            this.parameterTypes[i] = args[i].getClass();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZRpcRequest request = (ZRpcRequest) o;
        return Objects.equals(id, request.id) && Objects.equals(serviceInfo, request.serviceInfo) && Objects.equals(className, request.className) && Objects.equals(methodName, request.methodName) && Arrays.equals(parameterTypes, request.parameterTypes) && Arrays.equals(parameters, request.parameters);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, serviceInfo, className, methodName);
        result = 31 * result + Arrays.hashCode(parameterTypes);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public String toString() {
        return "ZRpcRequest{" +
                "id='" + id + '\'' +
                ", serviceInfo=" + serviceInfo +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
