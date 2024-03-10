package com.example.rpc;

import com.example.rpc.codec.Decoder;
import com.example.rpc.codec.Encoder;
import com.example.rpc.examples.Point;
import com.example.rpc.remoteObj.RequestMessage;
import org.junit.Test;

public class EncodeDecodeTest {
    @Test
    public void testEncodeThenDecode() {
        RequestMessage requestMessage = new RequestMessage(new Object[]{new Point(1,2), new Point(3, 4)}, 99L, "haasdasdasdashaha");
        System.out.println(requestMessage);
        // encode
        byte[] headerBodyBytes = Encoder.encode(requestMessage);
        assert headerBodyBytes!=null;
        // decode // TODO: why & 0xFF
        int bodySize = (headerBodyBytes[0] << 24) |
                ((headerBodyBytes[1] & 0xFF) << 16) |
                ((headerBodyBytes[2] & 0xFF) << 8) |
                (headerBodyBytes[3] & 0xFF);
        byte[] bodyBytes = new byte[bodySize];
        System.arraycopy(headerBodyBytes, 4, bodyBytes, 0, bodySize);
        RequestMessage decodedReqMsg = (RequestMessage) Decoder.decode(bodyBytes);
        System.out.println(decodedReqMsg);
        System.out.println(requestMessage.equals(decodedReqMsg));
    }
}
