
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TimeClient {

	public static String serviceName = "time-server";
	
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

		try {
			timeServer = (TimeServerInterface) Naming.lookup("rmi://localhost/time-server");
			String time = timeServer.time();
			System.out.println("Uhrzeit: " + time);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
}