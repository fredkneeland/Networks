import java.net.*;
import java.io.*;

public class Utilities
{
    public static void sendPacket(DatagramSocket socket, DatagramPacket packet)
    {
        System.out.println("Send Packet");
        try
        {
            socket.send(packet);
        }
        catch (Exception e)
        {
            System.out.print("Send failed" + e);
        }

    }

    public static void recievePacket(DatagramSocket socket, DatagramPacket packet)
    {
        System.out.println("Recieve Packet");
        try
        {
            socket.receive(packet);
        }
        catch (Exception e)
        {
            System.out.println("Error trying to recieve: " + e);
        }

    }
}
