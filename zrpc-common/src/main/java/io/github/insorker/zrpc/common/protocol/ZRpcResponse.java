package io.github.insorker.zrpc.common.protocol;

import java.io.Serial;
import java.io.Serializable;

public class ZRpcResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 7093577210957056011L;

    private String error;
    private Object result;

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
