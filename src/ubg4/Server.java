package ubg4;

import com.google.gson.Gson;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;
    public static HashMap<Long, String> messages;

    public static List<String> clientName;

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 5;
    private static final ClientThread[] threads = new ClientThread[maxClientsCount];

    public static void main(String args[]) {

        messages = new HashMap<Long, String>();
        clientName = new ArrayList<>();
        // The default port number.
        int portNumber = 8090;
        if (args.length < 1) {
            System.out.println("Usage: java ChatServer\n"
                    + "Now using port number=" + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }

    /*
     * Open a server socket on the portNumber (default 8090). Note that we can
     * not choose a port less than 1023 if we are not privileged users (root).
     */
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }

    /*
     * Create a client socket for each connection and pass it to a new client
     * thread.
     */
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new ClientThread(clientSocket, threads)).start();
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    Gson send1 = new Gson();
                    String[] con8={"Server full!"};
                    String json1 = send1.toJson(new Answer(503,000,con8));
                    os.println(json1);
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static void log(String s) {
        System.out.println(s);
    }
}