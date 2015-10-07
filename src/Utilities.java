import java.net.*;
import java.io.*;
import java.util.*;

public class Utilities
{
    public static DatagramSocket sockets;
    public static DatagramPacket packets;


    public static void sendPacket(DatagramSocket socket, DatagramPacket packet)
    {
        try
        {
            socket.send(packet);
        }
        catch (Exception e)
        {
            System.out.print("Send failed" + e);
        }

    }

    public static void sendPacketWithTimeout(final DatagramSocket socket, final DatagramPacket packet, final int timeout)
    {
        try {
            socket.send(packet);
            sockets = socket;
            packets = packet;
            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Your database code here
                    try {
                        sendPacketWithTimeout(socket, packet, timeout);
                    } catch (Exception e) {
                        System.out.println("Error trying to receive: " + e);
                    }
                }
            }, timeout /* ms the timer will run for*/);
        } catch (Exception e) { System.out.println(e); }
    }

    public static void receivePacket(DatagramSocket socket, DatagramPacket packet)
    {
        //System.out.println("In receivePacket Function");
        Timer timer = new Timer();
        sockets = socket;
        packets = packet;

        try {
            sockets.receive(packets);
        } catch (Exception e) {
            System.out.println("Error trying to receive: " + e);
        }

//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                // Your database code here
//                try {
//                    sockets.receive(packets);
//                } catch (Exception e) {
//                    System.out.println("Error trying to receive: " + e);
//                }
//            }
//        }, 5 /* ms the timer will run for*/);
//
//        try
//        {
//            Thread.sleep(100);
//        }
//        catch(Exception e)
//        {
//            System.out.println("Error sleeping");
//        }
    }

    public static void printServer(boolean[] sent, int windowStart, int windowSize)
    {
        String[] values = new String[windowSize];

        for (int i = 0; i < windowSize; i++)
        {
            int current = i + windowStart;
            if (current > sent.length)
            {
                values[i] = "-";
            }
            else
            {
                values[i] = "" + current;
                if (sent[i]) {
                    values[i] += "*";
                }
            }
        }

        System.out.print("[");

        for (int j = 0; j < windowSize-1; j++)
        {
            System.out.print(values[j] + ", ");
        }

        System.out.print(values[windowSize-1]);
        System.out.print("]");
        System.out.println("");
    }
}
