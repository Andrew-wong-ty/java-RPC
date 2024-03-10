package com.example.rpc.remoteObj;

import java.io.Serializable;

public class RemoteError implements Serializable {
    private final boolean success;
    private final String errMsg;

    public RemoteError(boolean success, String errMsg) {
        this.success = success;
        this.errMsg = errMsg;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "RemoteError{" +
                "success=" + success +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }
}
