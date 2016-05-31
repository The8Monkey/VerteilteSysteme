package ubg4;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by nieli on 30-May-16.
 */
public class MailboxClient {

    public static final int PORT = 8090;
    public static final String IP = "localhost";


    public static void main(String[] args) {
        BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
        loop: while(true) {
            try{
                Socket clientSocket = new Socket(IP,PORT);
                DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());


            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
