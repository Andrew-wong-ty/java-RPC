package com.example.rpc.client;

import com.example.rpc.codec.Decoder;
import com.example.rpc.codec.Encoder;
import com.example.rpc.remoteObj.ReplyMessage;
import com.example.rpc.remoteObj.RequestMessage;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class RPCClient {
    private Socket serverSocket;
    private DataInputStream inputStream;
    private OutputStream outputStream;
    private ReentrantLock mutex; // protects the following
    private ConcurrentHashMap<Long, BlockingQueue<Reply>> pending;
    private long currSequenceNumber;

    public RPCClient(String ip, int port) throws IOException {
        mutex = new ReentrantLock();
        pending = new ConcurrentHashMap<>();
        currSequenceNumber = 0;
        serverSocket = new Socket(ip, port);
        inputStream = new DataInputStream(new BufferedInputStream(serverSocket.getInputStream()));
        outputStream = serverSocket.getOutputStream();
        Executors.newSingleThreadExecutor().execute(this::readInput);
    }

    public BlockingQueue<Reply> sendRequest(String methodName, Object[] args) throws IOException {
        long seqNum;

        mutex.lock();
        seqNum = ++currSequenceNumber;
        BlockingQueue<Reply> blockingQueue = new ArrayBlockingQueue<Reply>(1);
        mutex.unlock();
        pending.put(seqNum, blockingQueue);

        RequestMessage reqMsg = new RequestMessage(methodName, args, seqNum);
//        System.out.println("client reqMsg="+reqMsg);

        try {
            byte[] data = Encoder.encode(reqMsg);
//            System.out.println(Arrays.toString(data));
            if(data==null) blockingQueue = null;
            outputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
            blockingQueue = null;
        }
        // free resources if fail
        if(blockingQueue==null) {
            pending.remove(seqNum);
        }

        return blockingQueue;
    }

    public ReplyMessage readOneReplyMsg() throws IOException {
        int bodySie = inputStream.readInt();
        byte[] bodyBytes = new byte[bodySie];
        int nRead = inputStream.read(bodyBytes); // read at most equal to the length of bodyByte
        if(nRead==-1 || nRead!=bodySie) {
            // EOF
            throw new EOFException();
        }
        ReplyMessage reqMsg = (ReplyMessage) Decoder.decode(bodyBytes);
        return reqMsg;
    }

    /**
     * A thread, which reads input from the socket connection; every time it reads one replyMsg
     * and notify the result using a blocking queue.
     */
    public void readInput() {
        while (true) {
            try {
                // read reply
                ReplyMessage replyMsg = readOneReplyMsg();
//                System.out.println("reply result = "+replyMsg);
                // notify using blocking queue
                BlockingQueue<Reply> blockingQueue = pending.getOrDefault(replyMsg.getSequenceNumber(), null);
                if(blockingQueue==null) {
                    System.out.println("Client fails to get blockingQueue");
                } else {
                    boolean success = blockingQueue.offer(new Reply(replyMsg.getResult(), replyMsg.getSequenceNumber()));
                    if(!success) {
                        System.out.println("Client fails to offer Reply to blockingQueue");
                    } else {
                        // delete entry in map
                        pending.remove(replyMsg.getSequenceNumber());
                    }
                }
//                break; // TODO: debug only
            } catch (Exception e) {
                // TODO: handle errors
                break;
            }
        }
    }

    public void stopConnection() throws IOException {
        serverSocket.close();
    }
}
