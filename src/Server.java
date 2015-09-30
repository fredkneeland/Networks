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


    public Server(int sendSocketPort, int recieveSocketPort, int portOfClient)
    {
        this.windowSize = 0;
        this.maximumSequenceNumb = 0;
        this.droppedPackets = 0;
        this.portOfClient = portOfClient;
        try
        {
            this.sendSocket = new DatagramSocket(sendSocketPort);
            this.recieveSocket = new DatagramSocket(recieveSocketPort);
            this.IPAddress = InetAddress.getByName("localhost");
        }
        catch (Exception e)
        {
            System.out.println("Failed in creating socket with:" + e);
        }

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


        // send initial data about window size to reciever
        byte[] setUpInfo = new byte[1024];
        setUpInfo[0] = (byte) this.windowSize;
        setUpInfo[1] = (byte) this.maximumSequenceNumb;
        DatagramPacket setUpPacket = new DatagramPacket(setUpInfo, setUpInfo.length, this.IPAddress, this.portOfClient);
        Utilities.sendPacket(this.sendSocket, setUpPacket);
        System.out.println("Send window’s size and maximum seq. number to the receiver");

        boolean recievedInitalAck = false;

        while (!recievedInitalAck)
        {
            byte[] ack = new byte[1024];
            DatagramPacket ackPacket = new DatagramPacket(ack, ack.length);
            try
            {
                this.recieveSocket.receive(ackPacket);
                if (ackPacket.getAddress() != null)
                {
                    recievedInitalAck = true;
                }
            }
            catch (Exception e)
            {
                System.out.println("Failed receiving setup ack: " + e);
            }
        }

        System.out.println("Receive confirmation from the receiver");

        while (true)
        {
            // send packets in window and update window when ack. arrives


        }
    }
}
