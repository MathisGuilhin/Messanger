package stream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastClient {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
            System.exit(1);
        }
        try {
            InetAddress groupAddr = InetAddress.getByName(args[0]);
            int groupPort = Integer.parseInt(args[1]);
            MulticastSocket s = new MulticastSocket(groupPort);
            s.joinGroup(groupAddr);
            // Build a datagram packet for a message
            // to send to the group
            String msg = "Hello";
            DatagramPacket hi = new
                    DatagramPacket(msg.getBytes(),
                    msg.length(), groupAddr, groupPort);
            // Send a multicast message to the group
            s.send(hi);

            while(true) {
                // Build a datagram packet for response
                byte[] buf = new byte[1000];
                DatagramPacket recv = new
                        DatagramPacket(buf, buf.length);
                // Receive a datagram packet response
                s.receive(recv);
                System.out.println(recv);
            }
            // OK, I'm done talking - leave the group
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }
}
