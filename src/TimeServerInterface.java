
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TimeServerInterface extends Remote {
    public String time() throws RemoteException;
}