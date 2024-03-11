# java-RPC

## Introduction
- A java RPC library implemented with persistent socket connection.

## Performance

- The concurrency performance for integer addition RPC on the Apple M2 chip is approximately `21,000` per second.

## Architecture
![image](https://github.com/Andrew-wong-ty/java-RPC/assets/78400045/0b7ecb4c-b297-4015-89e5-96a9caafcb32)
The image illustrates how it works by using an example.
- It first creates a `MyMath` Interface and implementation. The RPC client registers the interface and the RPC server registers the interface and its implementation. Once the server is running, the client can establish a connection by dialing it.
- When the client invokes the `Add` function, which adds two numbers, the client creates a unique sequence number `seq` and encodes `Add`'s arguments and function name into bytes (`body`). Then the header encodes the `body`'s size. Then the `header`+`body` as a whole is written to the `conn`. It then creates a `BlockingQueue` for this function to wait for return.
- Then on the server side, it reads the `body` and decodes out arguments and the function name. A goroutine uses the name to look up the correspondent function's reflection value and call it using arguments. Then the arguments are encoded and packaged in the same way to send to the client.
- Finally, on the client side, a goroutine decodes out a `body` consisting of return values, the sequence number `seq`, etc. Then `seq` is used to look up the previously mentioned `BlockingQueue`. Returns are sent to this `BlockingQueue`, and the function receives it and finally returns the result of `Add`.

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
    // conduct RPC call
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
## TODOs
1. RPC server registers multiple interfaces
2. Forcing interface throws RemoteException
