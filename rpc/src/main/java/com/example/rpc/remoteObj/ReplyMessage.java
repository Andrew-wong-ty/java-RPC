package com.example.rpc.remoteObj;

import java.io.Serializable;

public class ReplyMessage implements Serializable {
    private Object result;
    private long sequenceNumber;
    private RemoteError remoteError;


    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public RemoteError getRemoteError() {
        return remoteError;
    }

    public void setRemoteError(RemoteError remoteError) {
        this.remoteError = remoteError;
    }

//    @Override
//    public String toString() {
//        return "ReplyMessage{" +
//                "result=" + result +
//                ", sequenceNumber=" + sequenceNumber +
//                ", remoteError=" + remoteError +
//                '}';
//    }
}
