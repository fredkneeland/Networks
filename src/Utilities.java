import java.net.*;

public class Utilities
{
    /**
     * This function sends a packet
     */
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

    /**
     * This method receives a packet with a 50ms timeout
     */
    public static void receivePacket(DatagramSocket socket, DatagramPacket packet)
    {
        try {
            socket.receive(packet);
            socket.setSoTimeout(50);
        } catch (Exception e) {}
    }

    /**
     * This method prints out the current window and status of packets
     */
    public static void printServer(boolean[] sent, int windowStart, int windowSize)
    {
        String[] values = new String[windowSize];

        for (int i = 0; i < windowSize; i++)
        {
            int current = i + windowStart;
            if (current >= sent.length)
            {
                values[i] = "-";
            }
            else
            {
                values[i] = "" + current;
                if (sent[current]) {
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
