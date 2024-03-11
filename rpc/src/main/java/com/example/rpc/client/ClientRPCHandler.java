package com.example.rpc.client;

import com.example.rpc.remoteObj.ReplyMessage;
import com.example.rpc.remoteObj.RequestMessage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

public class ClientRPCHandler implements InvocationHandler {
    private final RPCClient client;
    public ClientRPCHandler(RPCClient client) {
        this.client = client;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().equals("toString")) return "noString!";
        Object result = null;
        try {

//            System.out.println("CLIENT: method= "+ method.getName());
//            System.out.println("CLIENT: args= "+ Arrays.toString(args));
            // send request
            BlockingQueue<Reply> blockingQueue = client.sendRequest(method.getName(), args);
            if(blockingQueue==null) return null;
            // wait for response
//            client.readInput();
            // block and read one from queue
            Reply reply = blockingQueue.take();
            result = reply.getResult();

        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " +
                    e.getMessage());
        }
        return result;
    }
}
