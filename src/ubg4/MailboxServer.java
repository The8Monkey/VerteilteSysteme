package ubg4;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by nieli on 30-May-16.
 */
public class MailboxServer {

    final int MAXCLIENTS = 5;

    public static void main(String[] args) throws Exception {
        final String name = "mailbox-server";
        final int Socket = 1337;
        String inval;
        String outval;
        ServerSocket welcomeSocket = new ServerSocket(Socket);
        Socket conSocket = null;
        try {
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(conSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(conSocket.getOutputStream());
            inval = inFromClient.readLine();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
