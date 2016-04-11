import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by User on 11.04.2016.
 */
public class TCPServer {
    public static void main(String[] args) throws IOException {
        String client;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(6789);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true){
            Socket connection = serverSocket.accept();
            BufferedReader brFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            DataOutputStream dosFromClient = new DataOutputStream(connection.getOutputStream());
            client = brFromClient.readLine();
            System.out.println("Recieved: " + client);
        }
    }
}
