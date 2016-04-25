import java.io.BufferedReader;
import java.io.DataOutputStream;
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
        DataOutputStream outToClient=null;
        ServerSocket welcomeSocket = new ServerSocket(5678);
        Socket conSocket = null;
        while(true){
            try {
                conSocket = welcomeSocket.accept();
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(conSocket.getInputStream()));
                outToClient = new DataOutputStream(conSocket.getOutputStream());
                inval = Integer.parseInt(inFromClient.readLine());
                System.out.print("recieved: " + inval+ "\n");
                if(inval<=0 || inval >=10000){
                    System.out.println("send: " + -2);
                    outToClient.writeBytes(-2 + "\n");
                }else {
                    outval = Fibonacci.fibonacci(inval);
                    System.out.println("send: " + outval);
                    outToClient.writeBytes(outval + "\n");
                }
            } catch(NumberFormatException e){
                System.out.println("send: " + -1);
                outToClient.writeBytes(-1 + "\n");
            } finally {
                conSocket.close();
            }
        }
    }
}
