package com.example.rpc;

import com.example.rpc.client.RPCClient;
import com.example.rpc.client.Stub;
import com.example.rpc.examples.MyMath;
import com.example.rpc.examples.Point;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

public class ClientTest {
    @Test
    public void testClientSendReq() throws IOException {
        MyMath myMath = (MyMath) Stub.create(MyMath.class, "127.0.0.1", 1234);
        Integer res = myMath.Add(1,2);
        Integer a = 1;
        System.out.println(res);
        Point point = myMath.AddPoint(new Point(1, 4), new Point(7,8));
        res = myMath.Add(88,988);
        System.out.println(res);
    }

    @Test
    public void testConcurrent() throws IOException, InterruptedException {
        final MyMath myMath = (MyMath) Stub.create(MyMath.class, "127.0.0.1", 1234);
        final Random random = new Random();
        int concurrency = 100_0000;
        Thread[] threads = new Thread[concurrency];
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < concurrency; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int x = random.nextInt(100), y = random.nextInt(100);
                    Integer res = myMath.Add(x, y);
                    assert x+y==res;
                }
            });
            thread.start();
            threads[i] = thread;
        }
        for (Thread thread : threads) {
            thread.join();
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("Execution time: " + elapsedTime + " milliseconds");
    }
}
