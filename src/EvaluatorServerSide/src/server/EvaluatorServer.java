package server;

import evaluator.Evaluator;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

public class EvaluatorServer {
	private static Registry RMI_REG;
	private static int REG_PORT = -1;
	private static Evaluator evaluate;
	
	public static Evaluator getEvaluator(){
		return evaluate;
	}
	
	public static boolean start(int RegistryPort){
		// Create RMI registry
		REG_PORT = RegistryPort;
		try{
			RMI_REG = LocateRegistry.createRegistry(RegistryPort);
		}catch(RemoteException e){
			System.out.println("Error initialising eval server registry: " + e);
			return false;
		}
		
		//Instantiate Evaluator
		try{
			evaluate = new Evaluator();
			Naming.rebind("rmi://localhost/evaluate", evaluate);
			
			System.out.println("Evaluator server active.");
		}catch(Exception e1){
			System.out.println("Evaluator server failed to launch: " + e1);
			return false;
		}
		
		return true;
	}
	
	public static boolean stop(){
		try{
			UnicastRemoteObject.unexportObject(RMI_REG, true);
			System.out.println("Evaluator server terminated.");
		}catch(NoSuchObjectException e){
			System.out.println("Unexpected error terminating registry on port#" + REG_PORT + ": " + e);
			return false;
		}
		
		return true;
	}

}
