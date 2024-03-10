package com.example.rpc.server;

import com.example.rpc.codec.Decoder;
import com.example.rpc.codec.Encoder;
import com.example.rpc.remoteObj.RemoteError;
import com.example.rpc.remoteObj.ReplyMessage;
import com.example.rpc.remoteObj.RequestMessage;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class RPCServer {
    private ServerSocket serverSocket;
    private Map<String, Method> methodMap;
    private Object implementation;
    private AtomicBoolean running;
    public RPCServer(Class<?> interfaceClass, Object implementation) {
        methodMap = new HashMap<>();
        register(interfaceClass, implementation);
    }
    public void start(int port) throws IOException {
        running = new AtomicBoolean(true); // TODO: only use for debug

        serverSocket = new ServerSocket(port);
        // TODO: thread
        accept();
    }

    public void stop() throws IOException {
        running.set(false);
        serverSocket.close();
    }

    private void accept() {
        while(running.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                serveClient(clientSocket);
            } catch (Exception exception) {
                exception.printStackTrace();
                break;
            }
        }
    }

    private void serveClient(Socket clientSocket) throws IOException {
        System.out.println("Server serve client, " +
                "local="+clientSocket.getLocalSocketAddress()+" remote="+clientSocket.getLocalSocketAddress());
        OutputStream outputStream = clientSocket.getOutputStream();
        DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        while(running.get()){
            // read requestMessage
            int bodySie = in.readInt();
            byte[] bodyBytes = new byte[bodySie];
            int nRead = in.read(bodyBytes); // read at most equal to the length of bodyByte
            if(nRead==-1 || nRead!=bodySie) {
                // EOF
                break;
            }
            RequestMessage reqMsg = (RequestMessage) Decoder.decode(bodyBytes);
            System.out.println("reqMsg= "+reqMsg);
            // invoke the method based on the reqMsg
            // TODO: thread
            ReplyMessage replyMsg = new ReplyMessage();
            replyMsg.setSequenceNumber(reqMsg.getSequenceNumber());
            try {
                Method method = methodMap.get(reqMsg.getMethodName());
                Object result = method.invoke(this.implementation, reqMsg.getArgs());
                replyMsg.setResult(result);
                replyMsg.setRemoteError(new RemoteError(true, "ok"));
            } catch (Exception e) {
                replyMsg.setRemoteError(new RemoteError(
                        false,
                        reqMsg.getMethodName()+e.getMessage()));
                replyMsg.setResult(null);
            }
            // write replyMsg to client
            System.out.println("reply= "+replyMsg);
            byte[] responseBytes = Encoder.encode(replyMsg);
            if(responseBytes==null) break;
            outputStream.write(responseBytes);
        }
    }



    // TODO: register interface and implementation
    private void register(Class<?> interfaceClass, Object implementation) {
        this.implementation = implementation;
        Class<?> implClass = implementation.getClass();
        // register all methods in the interface
        for(Method method: interfaceClass.getMethods()) {
            String methodName = method.getName();
            methodMap.put(methodName, null);
        }
        // register method implementations
        for(Method method: implClass.getMethods()) {
            String methodName = method.getName();
            if(methodMap.containsKey(methodName)) {
                methodMap.put(methodName, method);
            }
        }
    }

}
