package login;

import java.rmi.*;
import java.rmi.server.*;

public class Login extends UnicastRemoteObject implements LoginInterface {
	public  Login () throws RemoteException{}
	
	public boolean authenticate(String usrname, String usrpass){
		// Username & password are currently hardcoded. 
		// Database implementation was deemed too environment dependent to implement now.
		
		if(usrname.equals("lisa") && usrpass.equals("password1")){
			System.out.println("Successful login attempt.");
			return true;
		}
		System.out.println("Failed login attempt.");
		return false;
	}
}
