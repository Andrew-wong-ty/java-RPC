package com.example.rpc.codec;

import java.io.*;

public class Serializer {
    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

            // Write the object to the stream
            objectStream.writeObject(obj);
            objectStream.close();

            // Retrieve the serialized bytes
            return byteStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Object deserialize(byte[] bytes) {
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectStream = new ObjectInputStream(byteStream);

            // Read the object from the stream
            Object obj = objectStream.readObject();
            objectStream.close();

            // Cast the object to the appropriate class type
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
