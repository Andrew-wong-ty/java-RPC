package com.example.rpc.client;

import com.example.rpc.remoteObj.RequestMessage;
import com.example.rpc.server.RPCServer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Stub {
    private Class<?> interfaceClass;
    private String address;
    private static RPCClient client;
    public static Object create(Class<?> interfaceClass, String ip, int port) throws IOException {
        client = new RPCClient(ip, port);
//        client = null;
        // create the interface's proxy implementation
        return Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new ClientRPCHandler(client)
        );
    }
}
