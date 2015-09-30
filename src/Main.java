public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Hello World");
        int numb = 12;
        byte a = (byte) numb;
        System.out.println("Byte: " + a);
    }

    public static void runServer(String[] args)
    {
        int sendSocketPort = 1337;
        int receiveSocketPort = 1336;
        int portOfClient = 1337;
        Server server = new Server(sendSocketPort, receiveSocketPort, portOfClient);

        server.run();
    }
}
