public class Main
{
    public static void main(String[] args)
    {
        runServer(args);
        //runClient(args);
    }

    // this method is for running the server
    public static void runServer(String[] args)
    {
        int sendSocketPort = 1336;
        int receiveSocketPort = 1338;
        int portOfClient = 1337;
        Server server = new Server(sendSocketPort, receiveSocketPort, portOfClient);

        server.run();
    }

    // this method is for running the client
    public static void runClient(String[] args)
    {
        Client client = new Client();

        client.run();
    }
}
