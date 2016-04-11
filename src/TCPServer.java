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
    public static void main(String[] args) throws Exception {
        int inval;
        int outval;
        ServerSocket welcomeSocket = new ServerSocket(1234);

        while(true){
            Socket conSocket = welcomeSocket.accept();
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(conSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(conSocket.getOutputStream());
            inval = Integer.parseInt(inFromClient.readLine());
            System.out.print("recieved: "+inval);
            outval = Main.fibonacci(inval);
            System.out.println("send: "+outval);
            outToClient.writeBytes(outval + "\n");
        }
    }
}
