import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by User on 11.04.2016.
 */
public class TCPClient {
    public static void main(String[] args) throws Exception {
        int count = 0;
        int countfib;
        while(true) {
            BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
            Socket clientSocket = new Socket("localhost", 1234);
            DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            try {
                count = Integer.parseInt(fromUser.readLine());
                if(count>=10000){
                    System.out.println("Number to big");
                    throw new NumberFormatException();
                }
                toServer.writeBytes(count + "\n");
                countfib = Integer.parseInt(fromServer.readLine());
                System.out.printf("fibrotnatschi number at position %3d is: %5d %n", count, countfib);
            } catch(NumberFormatException e){
                toServer.writeBytes("\n");
                System.out.println("Please enter valid number");
            }
        }
    }

}
