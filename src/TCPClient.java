import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by User on 11.04.2016.
 */
public class TCPClient {
    public static void main(String[] args) throws Exception {
        int count = 0;
        int countfib;
        String[] arg;
        arg = new String[2];


        if(args.length==1){
            arg[0]=args[0];
            arg[1]="5678";
        }else
        if(args.length==0){
            arg[0]="localhost";
            arg[1]="5678";
        }else{
            arg[0]=args[0];
            arg[1]=args[1];
        }
        System.out.println("Tippen sie \"help\" für die zulässigen komandos ein.");
        loop:   while(true) {
            System.out.print("$>");
            Socket clientSocket = new Socket(arg[0], Integer.parseInt(arg[1]));

            DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
            String[] input = fromUser.readLine().split(" ");

            switch (input[0]){
                case "help":
                    System.out.println("Geben Sie \"ende\" ein um das Programm zu beenden.");
                    System.out.println("Geben Sie \"berechne <Zahl>\" ein um die Fibonatchizahl zu berechnen.");
                    toServer.writeBytes("\n");
                    break ;
                case "berechne":

                    try {
                        count = Integer.parseInt(input[1]);
                        if(count <=0 || count >=10000){
                            System.out.println("Fehlercode -2: ungültiger Zahlenbereich!(1-9999)");
                            toServer.writeBytes("\n");
                        }
                        toServer.writeBytes(count + "\n");
                        countfib = Integer.parseInt(fromServer.readLine());
                        if(countfib==-1){
                            System.out.println("Fehlercode -1: fehlerhafte eingabe!");
                        }else if(countfib==-2){
                            System.out.println("Fehlercode -2: ungültiger Zahlenbereich!(1-9999)");
                        }else {
                            System.out.printf("fibrotnatschi number at position %3d is: %5d %n", count, countfib);
                        }
                        } catch(NumberFormatException e){
                            toServer.writeBytes("\n");
                            System.out.println("Fehlercode -1: fehlerhafte eingabe!");
                    }

                    break ;
                case "ende":
                    break loop;
                default:
                    //System.out.println(Arrays.toString(input) + "ist keine richtige eingabe. Für Hilfe tippen sie help ein.");
                    printFail(input);
            }
        }
    }

    public static void printFail(String[] array){
        for (String s: array) {
            System.out.print(s+" ");
        }
        System.out.print("ist kein Befehl. Tippe help ein um alle Befehle angezeigt zu bekommen.\n");
    }
}
