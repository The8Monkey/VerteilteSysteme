package ubg3;

import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Created by nieli on 5/9/2016.
 * add this to VM-Options: "-Djava.security.policy=security.policy"
 */
public class PinnwandServer extends UnicastRemoteObject implements PinnwandInterface {

    public static void main(String[] args){
        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }
        try{
            final int lifeTime = 10000;

            String name = "pinnwand-server";
            PinnwandInterface pinnwand = new PinnwandServer(name,"password",lifeTime);

            final int port = 1337;

            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind(name,pinnwand);
            System.out.println("pinnwand bound to port: " + port);
        } catch (Exception e) {
            System.err.println("Something didnt work with the server");
            e.printStackTrace();
        }
    }

    public final int MAX_LENGTH = 160;
    public final int MAX_MSGCOUNT = 20;

    protected final int lifeTime;
    protected final String nameOfService;
    private final String password;
    protected HashMap<Long, String> messages;
    private boolean loggedIn;


    public PinnwandServer(String nameOfService, String password, int lifeTime) throws RemoteException{
        super();
        this.nameOfService = nameOfService;
        this.password = password;
        this.lifeTime = lifeTime;
        messages = new HashMap<Long, String>();
        loggedIn = false;
    }

    @Override
    public int login(String password) throws RemoteException {
        if(this.password.equals(password)){
            loggedIn = true;
            return 1;
        }
        loggedIn = false;
        return 0;
    }

    @Override
    public int getMessageCount() throws RemoteException {
        updateMsgs();
        return messages.size();
    }

    @Override
    public String[] getMessages() throws RemoteException {
        updateMsgs();
        return messages.values().toArray(new String[messages.values().size()]);
    }

    @Override
    public String getMessage(int index) throws RemoteException {
        updateMsgs();
        return messages.values().toArray(new String[messages.values().size()])[index];
    }

    @Override
    public boolean putMessage(String msg) throws RemoteException {
        if(updateMsgs() && msg.length() < MAX_LENGTH && loggedIn){
            messages.put(System.currentTimeMillis(), msg);
        } else {
            return false;
        }
        return true;
    }

    //returns true, is maximum count of messages isnt reached yet
    private boolean updateMsgs() throws RemoteException {
        Long current = System.currentTimeMillis();
        for(Long t : messages.keySet()){
            if(current - t > lifeTime){
                System.out.println("message: \"" + messages.get(t) + "\" is outdated and will be removed");
                messages.remove(t);
            }
        }
        return messages.size() < MAX_MSGCOUNT;
    }
}