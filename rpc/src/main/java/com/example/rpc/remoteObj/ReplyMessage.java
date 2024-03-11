package com.example.rpc.remoteObj;

import java.io.Serializable;

public class ReplyMessage implements Serializable {
    private String methodName;
    private Object result;
    private long sequenceNumber;
    private RemoteError remoteError;

    public ReplyMessage(String methodName, Object result, long sequenceNumber, RemoteError remoteError) {
        this.methodName = methodName;
        this.result = result;
        this.sequenceNumber = sequenceNumber;
        this.remoteError = remoteError;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object getResult() {
        return result;
    }


    public long getSequenceNumber() {
        return sequenceNumber;
    }


    public RemoteError getRemoteError() {
        return remoteError;
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
