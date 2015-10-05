
/**
 * Created by personal on 9/30/15.
 */
import java.net.*;


public class Client {
    int clientPort = 1337; // port where client will receive data on
    int windowSize = 1024; // 1024 bytes
    int maximumSequenceNumber;
    int windowStart = 0;
    int nextPacket = 0;
    InetAddress serverIP; // IP address of sender
    int serverOutputPort = -1; // port on the server
    int serverInputPort = 1338;
    public void run() {
        DatagramSocket receiverSocket;
        try {

            receiverSocket = new DatagramSocket(clientPort);
            byte[] receiveData = new byte[2]; // to receive the first packet, which will be 2 bytes in length
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            while(serverOutputPort ==-1) {
                Thread.sleep(1000);
                Utilities.receivePacket(receiverSocket, receivePacket);
                serverIP = receivePacket.getAddress();
                serverOutputPort = receivePacket.getPort();
               // System.out.println("Got first packet from sender, server IP is " + serverIP + " and port is " + serverOutputPort);
                windowSize = receiveData[0];
                maximumSequenceNumber = receiveData[1];
            }
            System.out.println("Received packet from sender, sender IP is "+serverIP);
            System.out.println("Max window size is "+windowSize+" and max sequence number is "+maximumSequenceNumber);
            byte[] sendData = new byte[1];
            serverIP = InetAddress.getByName("192.168.43.244");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIP, serverInputPort);
            DatagramSocket sendingSocket = new DatagramSocket(serverInputPort);
            Utilities.sendPacket(sendingSocket, sendPacket);
            System.out.println("Sent packet with one byte, " + sendData + " to IP " + serverIP + " port " + serverInputPort);
            byte[] toBeReceived = new byte[maximumSequenceNumber]; // prepare for max number of one-byte packets.
            System.out.println("Waiting for packet "+nextPacket+", window of size "+windowSize+" starting at "+windowStart);
            //getNextPacket(nextPacket,windowStart);
            while(true){
                byte[] nextData = new byte[1]; // prepare for one-byte packets
                DatagramPacket next = new DatagramPacket(nextData, nextData.length); // will be receiving one-byte packets from here on
                Utilities.receivePacket(receiverSocket, next);
                if(nextData[0]>0){
                    System.out.println("Sequence number is "+nextData[0]);
                }

                nextPacket++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private void getNextPacket(int nextPacket, int windowStart) {
        try {
            DatagramSocket receiverSocket = new DatagramSocket(clientPort);
            byte[] receiveData = new byte[1]; // prepare for one-byte packets
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); // will be receiving one-byte packets from here on
            Utilities.receivePacket(receiverSocket, receivePacket);
            nextPacket++;
             System.out.println("Got packet from server, "+receiveData+ " + serverOutputPort");
           // return nextPacket;
        }
        catch (Exception e){
            System.out.println("Error creating receive socket, "+e.getMessage());
        }
       // return 0; // if fail
    }
}