package com.example.rpc;

import com.example.rpc.client.RPCClient;
import com.example.rpc.client.Stub;
import com.example.rpc.examples.MyMath;
import org.junit.Test;

import java.io.IOException;

public class ClientTest {
    @Test
    public void testClientSendReq() throws IOException {
        MyMath myMath = (MyMath) Stub.create(MyMath.class, "127.0.0.1", 1234);
        Integer res = myMath.Add(1,2);
        System.out.println(res);
        res = myMath.Add(88,988);
        System.out.println(res);
    }
}
