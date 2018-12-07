package login;
import java.rmi.*;

public interface LoginInterface extends Remote {
	public boolean authenticate(String usrname, String usrpass) throws RemoteException;
}
