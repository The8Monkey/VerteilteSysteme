package ubg3;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by nieli on 5/9/2016.
 */
public interface PinnwandInterface extends Remote {

    public int login(String password) throws RemoteException;
    public int getMessageCount() throws RemoteException;
    public String[] getMessages() throws RemoteException;
    public String getMessage(int index) throws RemoteException;
    public boolean putMessage(String msg) throws RemoteException;

}
