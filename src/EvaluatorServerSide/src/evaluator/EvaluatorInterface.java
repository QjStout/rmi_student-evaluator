package evaluator;

import java.rmi.*;

public interface EvaluatorInterface extends Remote{
	public void setStudentId(String input) throws RemoteException;
	public void setAllMarks(String[][] usrInputs) throws RemoteException;
	public String getStudentId() throws RemoteException;
	public String[][] getAllMarks() throws RemoteException;
	public double getAvg() throws RemoteException;
	public String[][] getBest() throws RemoteException;
	public double getBestAvg() throws RemoteException;
	public String getEvalResults() throws RemoteException;
}
