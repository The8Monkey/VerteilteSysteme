
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

public class TimeServer extends UnicastRemoteObject
                        implements TimeServerInterface {

	private static final long serialVersionUID = 1L;

	public static String serviceName = "time-server";
	
	protected TimeServer() throws RemoteException {
		super();
	}

	@Override
	public String time() throws RemoteException {
		String now = new Date().toString();
		return now;
	}
	
	public static void main(String args[]) {		 

		Registry registry;
		
		TimeServerInterface timeServer;

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		try {
			if (args.length  > 0)
				registry = LocateRegistry.getRegistry(args[0]);
			else 
				registry = LocateRegistry.getRegistry("localhost");

			timeServer = (TimeServerInterface) registry.lookup(serviceName);

			String time = timeServer.time();
			System.out.println("Uhrzeit: " + time);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
}
