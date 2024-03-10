package com.example.rpc.client;

import com.example.rpc.codec.Decoder;
import com.example.rpc.codec.Encoder;
import com.example.rpc.remoteObj.ReplyMessage;
import com.example.rpc.remoteObj.RequestMessage;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class RPCClient {
    private Socket serverSocket;
    private DataInputStream inputStream;
    private OutputStream outputStream;
    private ReentrantLock mutex; // protects the following
    private long currSequenceNumber;

    public RPCClient(String ip, int port) throws IOException {
        mutex = new ReentrantLock();
        currSequenceNumber = 0;
        serverSocket = new Socket(ip, port);
        inputStream = new DataInputStream(new BufferedInputStream(serverSocket.getInputStream()));
        outputStream = serverSocket.getOutputStream();
    }

    public boolean sendRequest(String methodName, Object[] args) throws IOException {
        mutex.lock();
        currSequenceNumber++;
        RequestMessage reqMsg = new RequestMessage(args, currSequenceNumber, methodName);
        System.out.println("client reqMsg="+reqMsg);
        mutex.unlock();
        try {
            byte[] data = Encoder.encode(reqMsg);
            System.out.println(Arrays.toString(data));
            if(data==null) return false;
            outputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ReplyMessage readOneReply() throws IOException {
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

    public void readInput() {
        while (true) {
            try {
                // read reply
                ReplyMessage reqMsg = readOneReply();
                System.out.println("reply result = "+reqMsg);
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
