
/**
 * Created by personal on 9/30/15.
 */
import java.net.*;


public class Client {
    int clientPort = 1337; // port where client will receive data on
    int windowSize = 1024; // 1024 bytes
    int maximumSequenceNumber;
    InetAddress serverIP; // IP address of sender
    int serverPort; // port on the server

    public void run() {
        DatagramSocket receiverSocket;
        try {

            receiverSocket = new DatagramSocket(clientPort);
            byte[] receiveData = new byte[2]; // to receive the first packet, which will be 2 bytes in length
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            Utilities.receivePacket(receiverSocket, receivePacket);
            serverIP = receivePacket.getAddress();
            serverPort  = receivePacket.getPort();
            System.out.println("Got first packet from server, server IP is "+serverIP+" and port is "+serverPort);
            windowSize = receiveData[0];
            maximumSequenceNumber = receiveData[1];
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}