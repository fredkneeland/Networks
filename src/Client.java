import java.net.*;

// This is the class for all the client code
public class Client
{
    int clientPort = 1337; // port where client will receive data on
    int windowSize = 1024; // 1024 bytes
    int maximumSequenceNumber;
    int windowStart = 0;
    int nextPacket = 0;
    InetAddress serverIP; // IP address of sender
    int serverOutputPort = -1; // port on the server
    int serverInputPort = 1338;

    /**
     * This is the main function that will handle the receiving of the data
     */
    public void run()
    {
        DatagramSocket receiverSocket;
        try
        {
            receiverSocket = new DatagramSocket(clientPort);
            byte[] receiveData = new byte[2]; // to receive the first packet, which will be 2 bytes in length
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            while(serverOutputPort == -1)
            {
                Utilities.receivePacket(receiverSocket, receivePacket);
                serverIP = receivePacket.getAddress();
                serverOutputPort = receivePacket.getPort();
                windowSize = receiveData[0];
                maximumSequenceNumber = receiveData[1];
            }
            System.out.println("Received packet from sender, sender IP is " + serverIP);
            System.out.println("Max window size is "+windowSize+" and max sequence number is " + maximumSequenceNumber);
            char[] packetLog = new char[maximumSequenceNumber]; // make an array to store which packets have been received, acked
            for(int i = 0; i < packetLog.length; i++)
            {
                packetLog[i] = 'n'; // set to n for not received
            }
            byte[] sendData = new byte[1];
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIP, serverInputPort);
            DatagramSocket sendingSocket = new DatagramSocket(serverInputPort);
            Utilities.sendPacket(sendingSocket, sendPacket);
            System.out.println("Sent packet with one byte, " + sendData + " to IP " + serverIP + " port " + serverInputPort);
            System.out.println("Waiting for packet "+nextPacket+", window of size "+windowSize+" starting at "+windowStart);

            // main loop for client
            while(true)
            {
                byte[] nextData = new byte[1]; // prepare for one-byte packets
                DatagramPacket next = new DatagramPacket(nextData, nextData.length); // will be receiving one-byte packets from here on
                //Utilities.receivePacket(receiverSocket, next);
                receiverSocket.receive(next);
                if(nextData[0] > 0)
                {
                    int seqNum = receiveData[0]-1; // sequence number starts at 0, but need to send 1 to receive
                    System.out.print("Packet " + seqNum + " received. ");
                    packetLog[seqNum] = 'r'; // set to r for received
                    if(nextData[0]<(windowSize+windowStart))
                    {
                        DatagramPacket out = new DatagramPacket(nextData, nextData.length, serverIP, serverInputPort);
                        Utilities.sendPacket(sendingSocket, out); // send ack for packet received within window
                        System.out.print(" Sent ACK for " + seqNum);
                        packetLog[seqNum] = 'a'; // mark the packet received as ACKed
                        System.out.println(" Window at sequence number " + windowStart);
                        System.out.print("Sequence number: ");
                        for (int i = 0; i < packetLog.length; i++)
                        {
                            System.out.print(i + ", ");
                        }
                        System.out.println(); // return
                        System.out.print("Packet log:      ");
                        for (int i = 0; i < packetLog.length; i++)
                        {
                            if (i == windowStart)
                            {
                                System.out.print("[");
                            }
                            System.out.print(packetLog[i] + ", ");
                            if (i == (windowStart + (windowSize-1)))
                            {
                                System.out.print("]");
                            }
                        }
                        System.out.println();// return
                    }
                }

                nextPacket++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}