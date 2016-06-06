package ubg3;

import java.lang.reflect.Array;
import java.nio.file.AccessDeniedException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessControlException;
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
            final int lifeTime = 100000;

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
    private ArrayList<String> clients;


    public PinnwandServer(String nameOfService, String password, int lifeTime) throws RemoteException{
        super();
        this.nameOfService = nameOfService;
        this.password = password;
        this.lifeTime = lifeTime;
        messages = new HashMap<Long, String>();
        clients = new ArrayList<>();
    }

    @Override
    public int login(String password) throws RemoteException {
        if(this.password.equals(password) && password != null){

            try {
                clients.add(getClientHost());
            } catch (ServerNotActiveException e) {
                e.printStackTrace();
            }
            return 1;
        }
        try {
            if(clients.contains(getClientHost())){
                clients.remove(getClientHost());
            }
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getMessageCount() throws RemoteException {
        if(updateMsgs()) return messages.size();
        return -1;
    }

    @Override
    public String[] getMessages() throws RemoteException {
        if(updateMsgs()) return messages.values().toArray(new String[messages.values().size()]);
        return null;
    }

    @Override
    public String getMessage(int index) throws RemoteException, ArrayIndexOutOfBoundsException {
        if(updateMsgs()) return messages.values().toArray(new String[messages.values().size()])[index];
        return null;
    }

    @Override
    public boolean putMessage(String msg) throws RemoteException {
        if(updateMsgs() && msg.length() < MAX_LENGTH){
            messages.put(System.currentTimeMillis(), msg);
        } else {
            return false;
        }
        return true;
    }

    //returns false, if maximum count of messages is reached, or user isn't logged in
    private boolean updateMsgs() throws RemoteException {
        Long current = System.currentTimeMillis();

        try {
            if(!clients.contains(getClientHost())) throw new AccessControlException("not logged in");
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }

        Iterator it = messages.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(current - (Long)pair.getKey() > lifeTime){
                System.out.println("message: \"" + pair.getValue() + "\" is outdated and will be removed");
                it.remove();
            }
        }



        return messages.size() < MAX_MSGCOUNT;
    }
}