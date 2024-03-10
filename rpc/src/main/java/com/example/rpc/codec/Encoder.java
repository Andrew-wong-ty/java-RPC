package com.example.rpc.codec;

public class Encoder {
    /**
     * Encode an object to [header | body] where header is the size of body
     * represented by bytes
     * @param obj object being encoded
     * @return bytes array
     */
    public static byte[] encode(Object obj) {
        byte[] body =  Serializer.serialize(obj);
        if(body==null) return null;
        int bodySize = body.length;
        byte[] bytes = new byte[4+bodySize];
        bytes[0] = (byte) (bodySize>>24);
        bytes[1] = (byte) (bodySize>>16);
        bytes[2] = (byte) (bodySize>>8);
        bytes[3] = (byte) bodySize;
        System.arraycopy(body, 0, bytes, 4, bodySize);
        return bytes;
    }
}
