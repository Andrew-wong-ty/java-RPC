package com.example.rpc;

import com.example.rpc.client.RPCClient;
import com.example.rpc.server.RPCServer;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SocketTest {
    @Test
    public void clientServerTest() throws InterruptedException, IOException {
//        final int a = 1;
//        Thread serverThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                RPCServer rpcServer = new RPCServer();
//                try {
//                    rpcServer.start(1234);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        serverThread.start();
//        Thread.sleep(1000);
//        // client
//        RPCClient rpcClient = new RPCClient();
//        rpcClient.startConnection("127.0.0.1", 1234);
//        String response = rpcClient.sendMessage("hello server");
//        assertEquals("hello client", response);
//
//        // wait server done
//        serverThread.join();
    }
}
