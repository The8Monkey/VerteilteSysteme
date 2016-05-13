package ubg3;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by nieli on 5/9/2016.
 */
public class PinnwandServer extends UnicastRemoteObject implements PinnwandInterface {

    public final int MAX_LENGTH = 160;
    public final int MAX_MSGCOUNT = 20;

    protected final int lifeTime;
    protected final String nameOfService;
    private final String password;
    protected ArrayList<String> messages;


    public PinnwandServer(String nameOfService, String password, int lifeTime) throws RemoteException{
        super();
        this.nameOfService = nameOfService;
        this.password = password;
        this.lifeTime = lifeTime;
    }

    @Override
    public int login(String password) throws RemoteException {
        if(this.password.equals(password)) return 1;
        return 0;
    }

    @Override
    public int getMessageCount() throws RemoteException {
        return messages.size();
    }

    @Override
    public String[] getMessages() throws RemoteException {
        return (String[])messages.toArray();
    }

    @Override
    public String getMessage(int index) throws RemoteException {
        return messages.get(index);
    }

    @Override
    public boolean putMessage(String msg) throws RemoteException {
        if(messages.size() > MAX_MSGCOUNT | msg.length() > MAX_LENGTH) return false;
        return messages.add(msg);
    }

    public static void main(String[] args){
        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }

        try{
            String name = "pinnwand-server";
            PinnwandInterface pw = new PinnwandServer(name,"password",10);
            Registry registry = LocateRegistry.getRegistry(5678);
            registry.rebind(name,pw);
            System.out.println("pinnwand bound");
        } catch (Exception e) {
            System.err.println("Something didnt work with the server");
            e.printStackTrace();
        }
    }
}