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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class RPCServer {
    private ServerSocket serverSocket;
    private Object implementation;
    private AtomicBoolean running;
    private final Map<String, Method> methodMap;
    private final ExecutorService serveClientExecutor;
    private final ExecutorService methodCallerExecutor;
    public RPCServer(Class<?> interfaceClass, Object implementation) {
        methodCallerExecutor = Executors.newCachedThreadPool();
        serveClientExecutor = Executors.newCachedThreadPool();
        methodMap = new HashMap<>();
        register(interfaceClass, implementation);
    }
    public void start(int port) throws IOException {
        running = new AtomicBoolean(true);

        serverSocket = new ServerSocket(port);
        Executors.newSingleThreadExecutor().execute(this::accept);
    }

    public void stop() throws IOException {
        running.set(false);
        serverSocket.close();
    }

    private void accept() {
        while(running.get()) {
            try {
                // https://stackoverflow.com/questions/13910512/passing-parameter-to-java-thread
                final Socket clientSocket = serverSocket.accept();
                serveClientExecutor.execute(() -> {
                    try {
                        serveClient(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception exception) {
                exception.printStackTrace();
                break;
            }
        }
    }

    private RequestMessage readRequest(DataInputStream in) throws IOException {
        // read requestMessage
        int bodySie = in.readInt();
        byte[] bodyBytes = new byte[bodySie];
        int nRead = in.read(bodyBytes); // read at most equal to the length of bodyByte
        if(nRead==-1 || nRead!=bodySie) {
            // EOF
            return null;
        }
        RequestMessage reqMsg = (RequestMessage) Decoder.decode(bodyBytes);
        return reqMsg;
    }

    private void serveClient(Socket clientSocket) throws IOException {
//        System.out.println("Server serve client, " +
//                "local="+clientSocket.getLocalSocketAddress()+" remote="+clientSocket.getLocalSocketAddress());
        final OutputStream out = clientSocket.getOutputStream();
        DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        while(running.get()){
            // read requestMessage
            final RequestMessage reqMsg = readRequest(in);
            if(reqMsg==null) break;
//            System.out.println("reqMsg= "+reqMsg);
            // invoke the method based on the reqMsg
            methodCallerExecutor.execute(()-> {
                try {
                    callAndRespond(reqMsg, out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void callAndRespond(RequestMessage reqMsg, OutputStream outputStream) throws IOException {
        ReplyMessage replyMsg;
        try {
            Method method = methodMap.get(reqMsg.getMethodName());
            Object result = method.invoke(this.implementation, reqMsg.getArgs());
            replyMsg = new ReplyMessage(reqMsg.getMethodName(),
                    result,
                    reqMsg.getSequenceNumber(),
                    new RemoteError(true, "ok"));
        } catch (Exception e) {
            replyMsg = new ReplyMessage(reqMsg.getMethodName(),
                    null,
                    reqMsg.getSequenceNumber(),
                    new RemoteError(false, reqMsg.getMethodName()+e.getMessage()));
        }
        // write replyMsg to client
//        System.out.println("reply= "+replyMsg);
        byte[] responseBytes = Encoder.encode(replyMsg);
        if(responseBytes==null) return;
        outputStream.write(responseBytes);
    }



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
