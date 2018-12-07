package evaluator;

import java.rmi.*;
import java.rmi.server.*;
import java.util.Arrays;

public class Evaluator extends UnicastRemoteObject implements EvaluatorInterface {
	public Evaluator () throws RemoteException{}
	
	private String[][] allMarks;
	private String studentId;

	
/*-----------------------------
 *-----------Setters-----------
 *-----------------------------*/	
	
	/**
	 * Setter for studentId
	 */
	@Override
	public void setStudentId(String input) throws RemoteException{
		studentId = input;
	}
	
	/**
	 * Setter for allMarks
	 */
	@Override
	public void setAllMarks(String[][] usrInputs) throws RemoteException{
		/*
		 * allMarks map:
		 * 		String[i][0/1]
		 * 
		 * 		[i] = index of unit code & mark, set
		 * 		[0] = Unit code at current i
		 * 		[1] = Unit mark at current i
		 */
		allMarks = usrInputs;
	}
	
/*-----------------------------
 *-----------Getters-----------
 *-----------------------------*/
	
	/**
	 * Get student id
	 */
	@Override
	public String getStudentId() throws RemoteException{
		if(studentId != null){
			return studentId;
		}
		String notInit = "Student Id not initialised";
		return notInit;
	}
	
	/**
	 * Return all marks in order they were sent
	 */
	@Override
	public String[][] getAllMarks() throws RemoteException{
		return allMarks;
	}
	
	/**
	 * Return average of all units
	 */
	@Override
	public double getAvg() throws RemoteException{
		double allAvg = calcAverage(allMarks);
		return allAvg;
	}
	
	/**
	 * Gets the best 8 marks
	 */
	@Override
	public String[][] getBest() throws RemoteException{
		/*
		 * check for null
		 * pass all marks to sortHighLow
		 * Store 8 best marks in array
		 * return best marks
		 */
		
		if(allMarks == null){
			return null;
		}
		
		//Get sorted
		String[][] allSorted = sortHighLow(allMarks);
		
		//clone first 8
		String[][] bestMarks = Arrays.copyOf(allSorted, 8);
		
		return bestMarks;
	}
	
	/**
	 * Return average of best 8 units
	 */
	@Override
	public double getBestAvg() throws RemoteException{
		String[][] best = getBest();
		
		double bestAvg = calcAverage(best);		
		return bestAvg;
	}

	/**
	 * Get results of the evaluation
	 */
	@Override
	public String getEvalResults() throws RemoteException{
		// return result of evaluation
		String results = calcEvalResults(getAllMarks());
		return results;
	}	
	
/*-----------------------------
 *-----------Public------------
 *-----------------------------*/

	//None
	
/*-----------------------------
 *-----------Private-----------
 *-----------------------------*/
	
	/**
	 * Calculate the average of a set of marks 
	 */
	private static double calcAverage(String[][] marks){
		int totalMark = 0;
		int count = 0;
		
		if(marks == null){
			return -1;
		}
		
		for(String[] mark : marks){
			//Add mark to total then increment count
			totalMark += Integer.parseInt(mark[1]);
			count++;
		}
		
		if(totalMark > 0){
			double avg = (double) totalMark/count;
			double result = Math.round(avg*100.0)/100.0;
			
			return result;
		}

		return 0;
	}
	
	/**
	 * Calculate results
	 * @throws RemoteException 
	 */
	private String calcEvalResults(String[][] units) throws RemoteException{
		String results = "";
		
		/*
		 * Disqualified if failed more than 6 units
		 */
		int numFails = numUnitsFailed(units);
		
		if(numFails > 6){
			results = "Student Id: " + studentId + "\nNumber of units failed: " + numFails + "\nDOES NOT QUALIFY FOR HONORS STUDY!";
			return results;
		}
		
		/*
		 * Course average => 70
		 */
		double average = getAvg();
		if(average >= 70){
			results = "Student Id: " + studentId + "\nCourse Average: " + average + "\nQUALIFIED FOR HONOURS STUDY!";
			return results;
		}
		
		/*
		 * Best eight average >= 80 
		 */
		double bestAvg = getBestAvg();
		if(bestAvg >= 80){
			results = "Student Id: " + studentId + "\nCourse Average: " + average + "\nBest 8 Average: " + bestAvg + "\nMAY HAVE A GOOD CHANCE! Need further assessment!";
			return results;
		}
		
		/*
		 * Best eight average between 70-79
		 */
		if(bestAvg>=70 && bestAvg<80){
			results = "Student Id: " + studentId + "\nCourse Average: " + average + "\nBest 8 Average: " + bestAvg + "\nMAY HAVE A CHANCE! Must be carefully reassessed and get the coordinator's special permission!";
			return results;
		}
		
		/*
		 * Course average < 70 and best eight average < 70
		 */
		results = "Student Id: " + studentId + "\nCourse Average: " + average + "\nBest 8 Average: " + bestAvg + "\nDOES NOT QUALIFY FOR HONOURS STUDY! Try Masters by coursework.";
		
		return results;
	}
			
	/**
	 * Sort by highest to lowest mark
	 */
	private static String[][] sortHighLow(String[][] input){
		// copy array - array.clone() as it's a 2d array & will copy refs for [1]
		// Selection sort
		// return sorted array
		
		/*
		 * copy array
		 */
		int arrLength = input.length;
		String[][] marks = new String[arrLength][2];
		for(int i=0; i<arrLength; i++){
			marks[i][0] = input[i][0];
			marks[i][1] = input[i][1];
		}
		
		/*
		 * Selection sort
		 */
		int n = marks.length;
		for(int i=0; i<(n-1); i++){
			int max = i;
			
			for(int j=i+1; j<n; j++){
				int jMark = Integer.parseInt(marks[j][1]);
				int maxMark = Integer.parseInt(marks[max][1]);
				
				if(jMark > maxMark){
					max = j;
				}
			}
			
			if(max != i){
				/*
				 * Swap A & B
				 */
				//temp
				String unitCode, unitMark;
				
				//A -> temp
				unitCode = marks[i][0];
				unitMark = marks[i][1];
				
				//B -> A
				marks[i][0] = marks[max][0];
				marks[i][1] = marks[max][1];
				
				//temp -> B
				marks[max][0] = unitCode;
				marks[max][1] = unitMark;
			}
		}
		return marks;
	}
		
	/**
	 * Calculates the number of failed units
	 */
	private static int numUnitsFailed(String[][] units){ 
		int fails = 0;
		
		for(String[] unit : units){
			int mark = Integer.parseInt(unit[1]);
			
			if(mark < 50){
				fails++;
			}
		}
		
		return fails;
	}
}