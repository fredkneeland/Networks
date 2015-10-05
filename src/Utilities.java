import java.net.*;
import java.io.*;
import java.util.*;

public class Utilities
{
    public static DatagramSocket sockets;
    public static DatagramPacket packets;


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

    public static void receivePacket(DatagramSocket socket, DatagramPacket packet)
    {
        Timer timer = new Timer();
        sockets = socket;
        packets = packet;


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Your database code here
                try {
                    sockets.receive(packets);
                } catch (Exception e) {
                    System.out.println("Error trying to receive: " + e);
                }
            }
        }, 50 /* ms the timer will run for*/);
        try
        {
            Thread.sleep(50);
        }
        catch(Exception e)
        {
            System.out.println("Error sleeping");
        }
    }
}
