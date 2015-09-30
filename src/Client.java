/**
 * Created by personal on 9/30/15.
 */
import java.net.*;

public class Client {
    int clientPort = 1337; // port where client will receive data on
    int windowSize = 1024; // 1024 bytes
    InetAddress serverIP; // IP address of sender
    int serverPort; // port on the server

    public void run() {
        DatagramSocket receiverSocket;
        try {

            receiverSocket = new DatagramSocket(clientPort);
            byte[] receiveData = new byte[windowSize];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            Utilities.recievePacket(receiverSocket,receivePacket);
            serverIP = receivePacket.getAddress();
            serverPort  = receivePacket.getPort();

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}