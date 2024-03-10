package com.example.rpc;

import com.example.rpc.examples.MyMath;
import com.example.rpc.examples.MyMathImpl;
import com.example.rpc.server.RPCServer;
import org.junit.Test;

import java.io.IOException;

public class ServerTest {
    @Test
    public void startServer() throws IOException {
        RPCServer rpcServer = new RPCServer(MyMath.class, new MyMathImpl());
        rpcServer.start(1234);
    }
}
