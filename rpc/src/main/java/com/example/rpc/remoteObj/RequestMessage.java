package com.example.rpc.remoteObj;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class RequestMessage implements Serializable {
    private Object[] args;
    private long sequenceNumber;
    private String methodName;

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public RequestMessage(Object[] args, long sequenceNumber, String methodName) {
        this.args = args;
        this.sequenceNumber = sequenceNumber;
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "RequestMessage{" +
                "args=" + Arrays.toString(args) +
                ", sequenceNumber=" + sequenceNumber +
                ", methodName='" + methodName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestMessage that = (RequestMessage) o;
        return sequenceNumber == that.sequenceNumber && Arrays.equals(args, that.args) && Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(sequenceNumber, methodName);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
}
