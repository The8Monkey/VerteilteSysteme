import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by User on 11.04.2016.
 */
public class TCPClient {

    public static final int DEFAULT_PORT = 5678;
    public static final String DEFAULT_IP = "localhost";
    public static final int ERRORCODE_1 = -1;
    public static final int ERRORCODE_2 = -2;

    public static void main(String[] args) throws Exception {
        BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
        String ip = DEFAULT_IP;
        int port = DEFAULT_PORT;
        try {
            if(args[0].startsWith("-") | args[0] == "0"){
                throw new NumberFormatException();
            }
            ip = args[0];
            int port_tmp = Integer.parseInt(args[1]);
            if(port_tmp <= 0){
                throw new NumberFormatException();
            }
            port = Integer.parseInt(args[1]);
            System.err.println("set IP to: " + ip + ", and Port to: " + port);
        } catch(ArrayIndexOutOfBoundsException |NumberFormatException e) {
            System.err.println("invalid arguments, IP set to " + ip +" and port to " + port);
        }
        loop: while(true) {
            try {
                System.out.print("$> ");
                Socket clientSocket = new Socket(ip, port);
                String in[] = fromUser.readLine().split(" ", 2);
                DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                switch (in[0]) {
                    case "help":
                    case "hilfe":
                        System.out.println("Possible Commands:\n" +
                                "help / hilfe - shows this\n" +
                                "calc <number> / berechne <zahl> - calculates the fibonacci for <number / <zahl>\n" +
                                "exit / ende – exits the application");
                        toServer.writeBytes("\n");
                        break;
                    case "berechne":
                    case "calc":
                        try {
                            if(in.length != 2){
                                throw new NumberFormatException();
                            }
                            int inval = Integer.parseInt(in[1]);
                            toServer.writeBytes(inval + "\n");
                            int result = Integer.parseInt(fromServer.readLine());
                            if (result == -2) {
                                System.err.println("Error " + ERRORCODE_2 + ": input has to be between 0 and 50");
                                toServer.writeBytes("\n");
                                continue;
                            }
                            System.out.printf("fibrotnatschi number at position %3s is: %5d %n", in[1], result);
                        } catch (NumberFormatException e) {
                            System.err.println("Error " + ERRORCODE_1 + " Please enter valid number");
                            toServer.writeBytes("\n");
                            continue;
                        } finally {
                            break;
                        }
                    case "ende":
                    case "exit":
                        System.err.println("disconnected - Application terminated");
                        break loop;
                    default:
                        System.out.println("please enter valid prompt, type \"help\" if youre stupid");
                        toServer.writeBytes("\n");
                        break;
                }
            }catch (IOException e){
                System.err.println("Connection to Server lost - Application terminated");
                break loop;
            }

        }
    }
}
