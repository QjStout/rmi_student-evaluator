package server;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;

import java.rmi.RemoteException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class Main {
	//Authentication server
//	private static int AUTH_PORT = 1099;
	private static Text txtAuthPort;
	
	//Evaluation server
	private static evaluator.Evaluator evaluate;
//	private static int EVAL_PORT = 1098;
	private static Text txtEvalPort;
	
	//Output label
	private static Label lblMsgOut;

	//Table
	protected Shell shell;
	private static Table table;
	private static TableItem[] tblRows = new TableItem[8];
	
	//Buttons
	private static Button btnAuthStart;
	private static Button btnEvalStart;
	private Button btnBestEight;
	private Button btnAllMarks;
	private Button btnCourseAverage;
	private Button btnBestAverage;
	private Button btnResult;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get AUTH_PORT from txtAuthPort
	 */
	private static int getAuthPort(){
		int authPort = -1;
		String rawAuthPort = txtAuthPort.getText();
		boolean isNumeric = isNumeric(rawAuthPort);
		
		if(isNumeric){
			authPort = Integer.parseInt(rawAuthPort);
		}
		return authPort;
	}
	
	private static int getEvalPort(){
		int evalPort = -1;
		String rawEvalPort = txtEvalPort.getText();
		boolean isNumeric = isNumeric(rawEvalPort);
		
		if(isNumeric){
			evalPort = Integer.parseInt(rawEvalPort);
		}
		return evalPort;
	}
	
	/**
	 * Start Authentication server
	 */
	private static void startAuthServ(){
		//get user entered authPort value
		//IF authPort equals -1, print error message
		int authPort = getAuthPort();
		if(authPort == -1){
			lblMsgOut.setText("AuthPort invalid. Please enter a numeric string and try again.");
			return;
		}
		
		//attempt to start Authentication server
		boolean succ = AuthenticationServer.start(authPort);
		if(succ){
			lblMsgOut.setText("Authentication server enabled");
			btnAuthStart.setSelection(true);
		}
		else{
			lblMsgOut.setText("Authentication server failed to launch");
			btnAuthStart.setSelection(false);
		}

		//Enable evalServ start button
		btnEvalStart.setEnabled(true);
	}
	
	/**
	 * Stop Authentication server
	 */
	private static void stopAuthServ(){
		//get authPort value from AuthenticationServer
		boolean succ = AuthenticationServer.stop();
		
		if(succ){
			lblMsgOut.setText("Authentication server disabled");
			btnAuthStart.setSelection(false);
		}
		else{
			lblMsgOut.setText("Unexpected error encountered while attempting to terminate Authentication server");
		}		
	}
	
	/**
	 * Start Evaluation server
	 */
	private static void startEvalServ(){
		int evalPort = getEvalPort();
		if(evalPort == -1){
			lblMsgOut.setText("EvalPort invalid. Please enter a numeric string and try again.");
			return;
		}
		
		boolean succ = EvaluatorServer.start(evalPort);
		if(succ){
			lblMsgOut.setText("Evaluator server enabled");
			btnEvalStart.setSelection(true);
		}
		else{
			lblMsgOut.setText("Evaluator server failed to launch");
			btnEvalStart.setSelection(false);
		}
		
		//Get Evaluator object
		evaluate = EvaluatorServer.getEvaluator();
	}
	
	/**
	 * Stop Evaluation server
	 */
	private static void stopEvalServ(){
		boolean succ = EvaluatorServer.stop();
		if(succ){
			lblMsgOut.setText("Evaluator server disabled");
			btnEvalStart.setSelection(false);
		}
		else{
			lblMsgOut.setText("Unexpected error encounterd while attempting to terminate Evaluator server");
			btnEvalStart.setSelection(false);
		}
	}
	
	/**
	 * Check if String is numeric
	 * 
	 * Note: 
	 * 		I know that this is a terrible hack. #SorryNotSorry
	 */
	private static boolean isNumeric(String rawAuthPort) {
		try{
			Integer.parseInt(rawAuthPort);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	/**
	 * Display all marks
	 */
	private static void allMarks(){
		/*
		 * get marks from evaluator.Evaluator.getAllMarks()
		 * empty table
		 * FOR each row in allMarks
		 * 		DO add row to table
		 */
		
		try {
			String[][] allMarks = evaluate.getAllMarks();
			emptyTable();
			
			if(allMarks != null){
				for(String[] row : allMarks){
					addRowTbl(row[0], row[1]);
				}
			}
			else{
				lblMsgOut.setText("No marks available");
			}
		} catch (RemoteException e) {
			lblMsgOut.setText("Exception occurred while getting all marks: " + e);
			e.printStackTrace();
		}		
	}
	
	/**
	 * Display the top 8 marks
	 */
	private static void topEight(){
		/*
		 * get top eight marks from evaluator.Evaluator.getBest()
		 * empty table
		 * FOR each row in top eight
		 * 		DO add row to table
		 * 
		 */
		
		try {
			String[][] topEight = evaluate.getBest();
			emptyTable();
			
			if(topEight != null){
				for(String[] row : topEight){
					addRowTbl(row[0], row[1]);
				}
			}
			else{
				lblMsgOut.setText("No marks available");
			}
		} catch (RemoteException e) {
			lblMsgOut.setText("Exception occurred while getting top eight marks: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Display the average of all marks
	 */
	private static void allAverage(){
		/*
		 * get average from Evaluator
		 * display in lblMsgOut
		 */
		
		try {
			double allAvg = evaluate.getAvg();
			if(allAvg != -1){
				lblMsgOut.setText("Course average: " + allAvg);
			}
			else{
				lblMsgOut.setText("No marks available");
			}
		} catch (RemoteException e) {
			lblMsgOut.setText("Exception occurred while getting course average: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Display the average of the top 8 marks
	 */
	private static void bestAverage(){
		try {
			double bestAvg = evaluate.getBestAvg();
			if(bestAvg != -1){
				lblMsgOut.setText("Best 8 marks average: " + bestAvg);
			}
			else{
				lblMsgOut.setText("no marks available");
			}
		} catch (RemoteException e) {
			lblMsgOut.setText("Exception occurred while getting average of best eight marks: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Empty table before re-populating
	 */
	private static void emptyTable(){
		/*
		 * IF table !null THEN
		 * 		DO empty table
		 */
		if(!(table == null)){
			tblRows = null;
			table.removeAll();
		}
	}
	
	/**
	 * Add a row to the end of the table
	 */
	private static void addRowTbl(String unitCode, String unitMark){
		/*
		 * 	IF table is not null THEN
		 * 		DO create temporary array with an additional index position
		 * 		DO copy all current rows to temporary array
		 * 		DO add new row to temporary array, last index
		 * 		DO point tblRow to new array location
		 * 	ELSE
		 * 		DO initialise tblRow with one index position
		 * 		DO insert new row
		 */
		try {
			if(!(tblRows == null)){
				int numRows = tblRows.length;
				
				TableItem[] tempArray = new TableItem[numRows+1];
				System.arraycopy(tblRows, 0, tempArray, 0, numRows);
				tempArray[numRows] = new TableItem(table, SWT.NONE);
				tempArray[numRows].setText(new String[]{Integer.toString(numRows+1), unitCode, unitMark});
				
				tblRows = tempArray;
			}
			else{
				tblRows = new TableItem[1];
				tblRows[0] = new TableItem(table, SWT.NONE);
				tblRows[0].setText(new String[]{"1", unitCode, unitMark});
			}
		} catch (Exception e) {
			lblMsgOut.setText("Error encountered while adding row to table: " + e);
		}
	}
	
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 531);
		shell.setText("SWT Application");
		
		btnAuthStart = new Button(shell, SWT.TOGGLE);
		btnAuthStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				boolean btnSelected = btnAuthStart.getSelection();
				
				if(btnSelected){
					startAuthServ();
				}
				else{
					stopAuthServ();
				}
			}
		});
		btnAuthStart.setBounds(239, 30, 75, 25);
		btnAuthStart.setText("Start");
		
		Label lblAuthenticationServer = new Label(shell, SWT.NONE);
		lblAuthenticationServer.setAlignment(SWT.RIGHT);
		lblAuthenticationServer.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblAuthenticationServer.setBounds(77, 33, 141, 15);
		lblAuthenticationServer.setText("Authentication Server:");
		
		Label lblEvaluatorServer = new Label(shell, SWT.NONE);
		lblEvaluatorServer.setAlignment(SWT.RIGHT);
		lblEvaluatorServer.setText("Evaluator Server:");
		lblEvaluatorServer.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblEvaluatorServer.setBounds(77, 79, 141, 15);
		
		btnEvalStart = new Button(shell, SWT.TOGGLE);
		btnEvalStart.setEnabled(false);
		btnEvalStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean btnSelected = btnEvalStart.getSelection();
				
				if(btnSelected){
					startEvalServ();
				}
				else{
					btnEvalStart.setEnabled(false);
					stopEvalServ();
				}
			}
		});
		btnEvalStart.setText("Start");
		btnEvalStart.setBounds(239, 76, 75, 25);
		
		lblMsgOut = new Label(shell, SWT.BORDER | SWT.WRAP);
		lblMsgOut.setBounds(10, 398, 414, 84);
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 172, 414, 206);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn clmCount = new TableColumn(table, SWT.NONE);
		clmCount.setWidth(35);
		clmCount.setText("#");
		
		TableColumn tblclmnUnitCode = new TableColumn(table, SWT.NONE);
		tblclmnUnitCode.setWidth(182);
		tblclmnUnitCode.setText("Unit Code");
		
		TableColumn tblclmnUnitMark = new TableColumn(table, SWT.NONE);
		tblclmnUnitMark.setWidth(193);
		tblclmnUnitMark.setText("Unit Mark");
		
		txtAuthPort = new Text(shell, SWT.BORDER);
		txtAuthPort.setText("1099");
		txtAuthPort.setBounds(346, 30, 52, 25);
		
		txtEvalPort = new Text(shell, SWT.BORDER);
		txtEvalPort.setText("1098");
		txtEvalPort.setBounds(346, 76, 52, 25);
		
		btnBestEight = new Button(shell, SWT.NONE);
		btnBestEight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnBestEight.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				topEight();
			}
		});
		btnBestEight.setBounds(198, 128, 75, 25);
		btnBestEight.setText("Best Eight");
		
		btnAllMarks = new Button(shell, SWT.NONE);
		btnAllMarks.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				allMarks();
			}
		});
		btnAllMarks.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnAllMarks.setBounds(10, 128, 75, 25);
		btnAllMarks.setText("All Marks");
		
		btnCourseAverage = new Button(shell, SWT.NONE);
		btnCourseAverage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				allAverage();
			}
		});
		btnCourseAverage.setBounds(91, 128, 101, 25);
		btnCourseAverage.setText("Course Average");
		
		btnBestAverage = new Button(shell, SWT.NONE);
		btnBestAverage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bestAverage();
			}
		});
		btnBestAverage.setBounds(279, 128, 75, 25);
		btnBestAverage.setText("Best Average");
		
		btnResult = new Button(shell, SWT.NONE);
		btnResult.setBounds(360, 128, 64, 25);
		btnResult.setText("Result");

	}
}
