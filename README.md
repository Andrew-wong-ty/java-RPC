# java-RPC

## Introduction
- A java RPC library implemented with persistent socket connection.

## Performance

- The concurrency performance for integer addition RPC on the Apple M2 chip is approximately `21,000` per second.

## Architecture
![image](https://github.com/Andrew-wong-ty/go-RPC/assets/78400045/32d098f2-08f9-432c-9a91-fbe5b3f0f197)
The image illustrates how it works by using an example.
- It first creates a `Math` Interface and implementation. The RPC client registers the interface and the RPC server registers the interface and its implementation. Once the server is running, the client can establish a connection by dialing it.
- When the client invokes the `Add` function, which adds two numbers, the client creates a unique sequence number `seq` and encodes `Add`'s arguments and function name into bytes (`body`). Then the header encodes the `body`'s size. Then the `header`+`body` as a whole is written to the `conn`. It then creates a `channel` for this function to wait until returns are received or timeout.
- Then on the server side, it reads the `body` and decodes out arguments and the function name. A goroutine uses the name to look up the correspondent function's reflection value and call it using arguments. Then the arguments are encoded and packaged in the same way to send to the client.
- Finally, on the client side, a goroutine decodes out a `body` consisting of return values, the sequence number `seq`, etc. Then `seq` is used to look up the previously mentioned `channel`. Returns are sent to this `channel`, and the function receives it and finally returns the result of `Add`.

Note: the previous description simplified the process to convey the general idea, which differs from the actual implementation.


## Usage Example
1. Coding the Service Endpoint Interface and Class.

```java
// MyMath.java
public interface MyMath {
    Integer Add(Integer x, Integer y);
    Point AddPoint(Point x, Point y);
}

// MyMathImpl.java
public class MyMathImpl implements MyMath {
  @Override
  public Integer Add(Integer x, Integer y) {
    return x+y;
  }
  @Override
  public Point AddPoint(Point x, Point y) {
    return new Point(x.getX()+y.getX(), x.getY()+y.getY());
  }
}
```

2. Create RPC server and client, and invoke RPC.

```java
public class Main {
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

```
