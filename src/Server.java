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


    /**
     * Constructor to set up the various sockets
     */
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

    /**
     * Main function that will run the Server code
     */
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

        // go until handshake packet has been acked
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
            // wait for half a second before resending initial handshake
            while (startTime > (currentTime - 500) && !recievedInitalAck)
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
        }

        System.out.println("Receive confirmation from the receiver");

        int currentWindowStart = 0;
        // Main loop where we send out packets and receive acks
        while (true)
        {
            int currentDate = (int) System.currentTimeMillis();
            // send packets in window and update window when ack. arrives
            for (int i = 0; i < this.windowSize; i++)
            {
                int currentPacketIndex = (currentWindowStart  + i) % this.maximumSequenceNumb;
                // if we haven't sent the current packet and are going to drop this packet
                if (!this.droppedPacket && currentPacketIndex == this.droppedPackets)
                {
                    this.droppedPacket = true;
                    this.packetSent[currentPacketIndex] = true;
                    System.out.println("Packet dropped:" + currentPacketIndex);
                    this.packetSentTimer[currentPacketIndex] = currentDate;
                }
                // if we haven't sent out a packet in the window then send it
                else if (!this.packetSent[currentPacketIndex])
                {
                    // send packet
                    byte[] info = new byte[1];
                    info[0] = (byte) (currentPacketIndex + 1);
                    DatagramPacket packet = new DatagramPacket(info, info.length, this.IPAddress, this.portOfClient);
                    Utilities.sendPacket(this.sendSocket, packet);
                    this.packetSentTimer[currentPacketIndex] = currentDate;
                    this.packetSent[currentPacketIndex] = true;
                    System.out.print("Packet " + (info[0] - 1) + " is sent, window ");
                    Utilities.printServer(this.packetSent, currentWindowStart, this.windowSize);
                }
                // if we have sent a packet but haven't received ack after time out then resend
                else if (!this.ackArrived[currentPacketIndex] && this.packetSentTimer[currentPacketIndex] < (currentDate - 500))
                {
                    // we have passed the timer for this packet and should resend
                    byte[] info = new byte[1];
                    info[0] = (byte) (currentPacketIndex + 1);
                    DatagramPacket packet = new DatagramPacket(info, info.length, this.IPAddress, this.portOfClient);
                    Utilities.sendPacket(this.sendSocket, packet);
                    this.packetSentTimer[currentPacketIndex] = currentDate;
                    System.out.println("Packet " + (info[0] - 1) + " times out, resend packet " + (info[0] - 1));
                }
            }

            // update window for received packet
            byte[] ack = new byte[1];
            DatagramPacket ackPacket = new DatagramPacket(ack, ack.length);
            Utilities.receivePacket(this.recieveSocket, ackPacket);

            // if we received an ack then register that packet as received and update window if relevant
            if (ack[0] > 0)
            {
                int packetNumb = ((int) ack[0]) - 1;
                this.ackArrived[packetNumb] = true;

                // check to see if and how far the window should be updated
                for (int i = 0; i < this.windowSize; i++)
                {
                    if (currentWindowStart < this.ackArrived.length && this.ackArrived[currentWindowStart])
                    {
                        currentWindowStart++;
                    }
                }

                System.out.print("Ack " + (ack[0] - 1) + " is received, window ");
                Utilities.printServer(this.packetSent, currentWindowStart, this.windowSize);
            }

            // if all packets have been sent then we are done and can exit
            if (currentWindowStart >= this.maximumSequenceNumb)
            {
                System.out.println("we are done");
                break;
            }

        }
    }
}