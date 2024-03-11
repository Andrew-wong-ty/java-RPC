package com.example.rpc;

import com.example.rpc.client.Stub;
import com.example.rpc.examples.MyMath;
import com.example.rpc.examples.MyMathImpl;
import com.example.rpc.server.RPCServer;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class RPCTest {
    @Test
    public void testConcurrent() throws InterruptedException, IOException {
        // create server
        RPCServer rpcServer = new RPCServer(MyMath.class, new MyMathImpl());
        rpcServer.start(1234);
        // create client
        final MyMath myMath = (MyMath) Stub.create(MyMath.class, "127.0.0.1", 1234);
        final Random random = new Random();
        int concurrency = 100_0000;
        Thread[] threads = new Thread[concurrency];
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < concurrency; i++) {
            Thread thread = new Thread(() -> {
                int x = random.nextInt(100), y = random.nextInt(100);
                Integer res = myMath.Add(x, y);
                assert x+y==res;
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
    public static void main(String[] args) throws IOException, InterruptedException {
        // create server
        RPCServer rpcServer = new RPCServer(MyMath.class, new MyMathImpl());
        rpcServer.start(1234);
        // create client
        final MyMath myMath = (MyMath) Stub.create(MyMath.class, "127.0.0.1", 1234);
        final Random random = new Random();
        int concurrency = 100_0000;
        Thread[] threads = new Thread[concurrency];
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < concurrency; i++) {
            Thread thread = new Thread(() -> {
                int x = random.nextInt(100), y = random.nextInt(100);
                Integer res = myMath.Add(x, y);
                assert x+y==res;
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
