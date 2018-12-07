package server;

import login.Login;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

public class AuthenticationServer {
	private static Registry RMI_REG;
	private static int REG_PORT = -1;
	private static Login login;
	
	public static boolean start(int RegistryPort){
		// Create RMI registry
		REG_PORT = RegistryPort;
		try{
			RMI_REG = LocateRegistry.createRegistry(RegistryPort);				
		}catch(RemoteException e){
			System.out.println("Error initialising auth server registry: " + e);
			return false;
		}
		
		// Instantiate Login
		try{
			login = new Login();
			Naming.rebind("rmi://localhost/login", login);
			
			System.out.println("Authentication server active.");
		}catch(Exception e1){
			System.out.println("Authentication server failed to launch: " + e1);
			return false;
		}
		
		return true;
	}
	
	public static boolean stop(){
		try {
			UnicastRemoteObject.unexportObject(RMI_REG, true);
			System.out.println("Authentication server terminated.");
		} catch (NoSuchObjectException e) {
			System.out.println("Unexpected error terminating registry on port#" + REG_PORT + ": " + e);
			return false;
		}
		
		return true;
	}
}
