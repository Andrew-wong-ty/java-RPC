package com.example.rpc.client;

public class Reply {
    private final Object result;
    private final long sequenceNumber;

    public Reply(Object result, long sequenceNumber) {
        this.result = result;
        this.sequenceNumber = sequenceNumber;
    }


    public Object getResult() {
        return result;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }
}
