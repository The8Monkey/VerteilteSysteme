package ubg3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by nieli on 12-May-16.
 */
public class PinnwandClient {

    public static void main(String[] args){

        PinnwandInterface pinnwandServer = null;

        try{
            String name = "pinnwand-server";
            int port = 1337;
            String ip = "localhost";
            Registry registry = LocateRegistry.getRegistry(ip, port);
            pinnwandServer = (PinnwandInterface) registry.lookup(name);
            System.out.println("connected to server: \"" + name + "\" \nPort: " + port + "\nIP: " + ip);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
        loop: while(true){
            try {
                String in[] = fromUser.readLine().split(" ", 2);
                switch(in[0].toLowerCase()){
                    case "login":
                        try{
                            if(in.length != 2){
                                throw new NumberFormatException();
                            }
                            if(pinnwandServer.login(in[1]) == 1){
                                System.out.println("successfully logged in.");
                            } else {
                                throw new NumberFormatException();
                            }
                        } catch(NumberFormatException e){
                            System.err.println("login failed");
                        } finally {
                            break;
                        }

                    case "put":
                        if(in.length < 2){
                            System.err.println("invalid message, try again..");
                            break;
                        }
                        String message = "";
                        for(int i = 1; i < in.length; i++){
                            message += in[i] + " ";
                        }
                        if(pinnwandServer.putMessage(message)){
                            System.out.println("entry \"" + message + "\" successful");
                        } else {
                            System.err.println("entry failed, maybe forgot to log in?");
                        }
                        break;

                    case "get":
                        if(in.length < 2) {
                            System.out.println("All Messages: \n");
                            for (String msg : pinnwandServer.getMessages()) {
                                System.out.println(msg);
                            }
                        }else{
                            try{
                                System.out.println("message at index: " + in[1] + ": " + pinnwandServer.getMessage(Integer.parseInt(in[1])));
                            }catch (NumberFormatException e) {
                                System.err.println("Error: Please enter valid Index");
                            }
                        }
                        break;

                    case "help":
                        System.out.println("Possible Commands:");
                        System.out.println("help - shows this");
                        System.out.println("login (password) - log in to post messages");
                        System.out.println("get (index optional) - posts all messages or the one with given index");
                        System.out.println("exit - terminated this client");
                        break;

                    case "exit":
                        System.out.println("client will be terminated");
                        break loop;
                    default:
                        System.out.println("please enter valid prompt, type \"help\" if you're stupid");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
