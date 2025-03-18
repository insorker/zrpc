package io.github.insorker.zrpc.common.protocol;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ZRpcResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 7093577210957056011L;
    private String id;
    private String requestId;
    private String error;
    private Object result;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZRpcResponse that = (ZRpcResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(requestId, that.requestId) && Objects.equals(error, that.error) && Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requestId, error, result);
    }

    @Override
    public String toString() {
        return "ZRpcResponse{" +
                "id='" + id + '\'' +
                ", requestId='" + requestId + '\'' +
                ", error='" + error + '\'' +
                ", result=" + result +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
