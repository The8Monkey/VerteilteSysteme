import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by User on 11.04.2016.
 */
public class TCPClient {
    public static void main(String[] args) throws Exception {
        BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
        String ip = "localhost";
        int port = 5678;
        if(args.length == 2){
            ip = args[0];
            port = Integer.parseInt(args[1]);
            System.err.println("set IP to: " + ip + ", and Port to: " + port);
        } else {
            System.err.println("invalid arguments, IP set to localhost and port to 5678");
        }
        loop: while(true) {
            System.out.print("$> ");
            Socket clientSocket = new Socket(ip, port);
            String in[] = fromUser.readLine().split(" ",2);
            DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            switch(in[0]){
                case "help":
                    System.out.println("mögliche Befehle:\n" +
                            "hilfe - Bedienungshilfe wird ausgegeben\n" +
                            "berechne <zahl> - berechnet fibonacci für <zahl>\n" +
                            "ende – beendet die Anwendung");
                    toServer.writeBytes("\n");
                    break;
                case "berechne":
                    try {
                        int inval = Integer.parseInt(in[1]);
                        toServer.writeBytes(inval + "\n");
                        int result = Integer.parseInt(fromServer.readLine());
                        if(result == -2){
                            System.err.println("Error -2: input has to be between 0 and 50");
                            toServer.writeBytes("\n");
                            continue;
                        }
                        System.out.printf("fibrotnatschi number at position %3s is: %5d %n", in[1], result);
                    } catch (NumberFormatException e){
                        System.err.println("Error -1: Please enter valid number");
                        toServer.writeBytes("\n");
                        continue;
                    } finally {
                        break;
                    }
                case "ende":
                    System.err.println("disconnected - Application terminated");
                    break loop;
                default:
                    System.out.println("please enter valid prompt, type \"help\" if youre stupid");
                    toServer.writeBytes("\n");
                    break;
            }

        }
    }
}
