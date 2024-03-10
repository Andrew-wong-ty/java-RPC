package com.example.rpc.codec;

public class Decoder {
    /**
     * Decode a bytes array into an Object
     * @param data the bytes
     * @return object
     */
    public static Object decode(byte[] data) {
        return Serializer.deserialize(data);
    }
}
