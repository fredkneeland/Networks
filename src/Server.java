import java.util.*;
import java.net.*;

public class Server
{
    int windowSize;
    int maximumSequenceNumb;
    int droppedPackets;
    InetAddress IPAddress;
    int portOfClient;
    DatagramSocket sendSocket;
    DatagramSocket recieveSocket;
    boolean[] packetSent;
    boolean[] ackArrived;
    int[] packetSentTimer;
    boolean droppedPacket;


    public Server(int sendSocketPort, int receiveSocketPort, int portOfClient)
    {
        // initialize defaults
        this.windowSize = 0;
        this.maximumSequenceNumb = 0;
        this.droppedPackets = 0;
        this.portOfClient = portOfClient;

        // create sockets
        try
        {
            this.sendSocket = new DatagramSocket(sendSocketPort);
            this.recieveSocket = new DatagramSocket(receiveSocketPort);
            this.IPAddress = InetAddress.getByName("192.168.43.244"); // 153.90.54.159
        }
        catch (Exception e)
        {
            System.out.println("Failed in creating socket with:" + e);
        }
        droppedPacket = false;

    }

    public void run()
    {
        Scanner in = new Scanner(System.in);
        // ask for set up data
        System.out.println("Enter the window’s size on the sender:");
        this.windowSize = in.nextInt();
        System.out.println("Enter the maximum sequence number on the sender:");
        this.maximumSequenceNumb = in.nextInt();
        System.out.println("Select the packet(s) that will be dropped: ");
        this.droppedPackets = in.nextInt();

        // setup arrays for packet sending
        this.packetSent = new boolean[maximumSequenceNumb];
        this.packetSentTimer = new int[maximumSequenceNumb];
        this.ackArrived = new boolean[maximumSequenceNumb];
        for (int i = 0; i < packetSent.length; i++)
        {
            this.packetSent[i] = false;
            this.ackArrived[i] = false;
        }

        boolean recievedInitalAck = false;

        // go until first packet has been acked
        while (!recievedInitalAck)
        {
            // send initial data about window size to reciever
            byte[] setUpInfo = new byte[2];
            setUpInfo[0] = (byte) this.windowSize;
            setUpInfo[1] = (byte) this.maximumSequenceNumb;
            DatagramPacket setUpPacket = new DatagramPacket(setUpInfo, setUpInfo.length, this.IPAddress, this.portOfClient);
            Utilities.sendPacket(this.sendSocket, setUpPacket);
            System.out.println("Send window’s size and maximum seq. number to the receiver: " + this.IPAddress);

            int startTime = (int) System.currentTimeMillis();
            int currentTime = (int) System.currentTimeMillis();
            // wait for one second before resending
            while (startTime > (currentTime - 1000) && !recievedInitalAck)
            {
                byte[] ack = new byte[1];
                DatagramPacket ackPacket = new DatagramPacket(ack, ack.length);

                try
                {
                    Utilities.receivePacket(recieveSocket, ackPacket);
                    if (ackPacket.getPort() != -1)
                    {
                        System.out.println("Received ack: " + ackPacket.getPort() + " ack:" + ack[0]);
                        recievedInitalAck = true;
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Failed receiving setup ack: " + e);
                }
                currentTime = (int) System.currentTimeMillis();
                System.out.println("Current time:" + currentTime + " StartTime: " + startTime);
            }
            System.out.println("After while");
        }

        System.out.println("Receive confirmation from the receiver");

        int currentWindowStart = 0;
        while (true)
        {
            int currentDate = (int) System.currentTimeMillis();
            // send packets in window and update window when ack. arrives
            for (int i = 0; i < this.windowSize; i++)
            {
                int currentPacketIndex = (currentWindowStart  + i) % this.maximumSequenceNumb;
                // if we haven't sent the current packet
                if (!this.droppedPacket && currentPacketIndex == this.droppedPackets)
                {
                    this.droppedPacket = true;
                    this.packetSent[currentPacketIndex] = true;
                    System.out.println("Packet dropped:" + currentPacketIndex);
                    this.packetSentTimer[currentPacketIndex] = currentDate;
                }
                else if (!this.packetSent[currentPacketIndex])
                {
                    // send packet
                    byte[] info = new byte[1];
                    info[0] = (byte) (currentPacketIndex + 1);
                    DatagramPacket packet = new DatagramPacket(info, info.length, this.IPAddress, this.portOfClient);
                    Utilities.sendPacket(this.sendSocket, packet);
                    System.out.print("Packet " + info[0] + " is sent, window ");
                    Utilities.printServer(this.packetSent, currentWindowStart, info[0]);
                    this.packetSentTimer[currentPacketIndex] = currentDate;
                    this.packetSent[currentPacketIndex] = true;
                }
                else if (!this.ackArrived[currentPacketIndex] && this.packetSentTimer[currentPacketIndex] < (currentDate - 500))
                {
                    // we have passed the timer for this packet and should resend
                    byte[] info = new byte[1];
                    info[0] = (byte) (currentPacketIndex + 1);
                    DatagramPacket packet = new DatagramPacket(info, info.length, this.IPAddress, this.portOfClient);
                    Utilities.sendPacket(this.sendSocket, packet);
                    this.packetSentTimer[currentPacketIndex] = currentDate;
                    System.out.println("Resent packet: " + info[0] + ", window ");
                    Utilities.printServer(this.packetSent, currentWindowStart, info[0]);
                }
            }

            // update window for received packet
            byte[] ack = new byte[1];
            DatagramPacket ackPacket = new DatagramPacket(ack, ack.length);
            Utilities.receivePacket(this.recieveSocket, ackPacket);

            if (ack[0] > 0)
            {
                int packetNumb = ((int) ack[0]) - 1;
                if (packetNumb == currentWindowStart)
                {
                    currentWindowStart++;
                }
                this.ackArrived[packetNumb] = true;

                System.out.println("Ack " + ack[0] + " is received, window ");
                Utilities.printServer(this.packetSent, currentWindowStart, ack[0]);
            }

        }
    }
}