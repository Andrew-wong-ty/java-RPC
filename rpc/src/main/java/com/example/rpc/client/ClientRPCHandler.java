package com.example.rpc.client;

import com.example.rpc.remoteObj.ReplyMessage;
import com.example.rpc.remoteObj.RequestMessage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ClientRPCHandler implements InvocationHandler {
    private final RPCClient client;
    public ClientRPCHandler(RPCClient client) {
        this.client = client;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {

            System.out.println("method= "+ method.getName());
            System.out.println("args= "+ Arrays.toString(args));
            // send request
            boolean res = client.sendRequest(method.getName(), args);
            if(!res) return null;
            // wait for response
            ReplyMessage reqMsg = client.readOneReply();
            result = reqMsg.getResult();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " +
                    e.getMessage());
        } finally {
            System.out.println("after method " + method.getName());
        }
        return result;
    }
}
