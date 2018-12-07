package evaluator;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class EvaluatorClient {

	protected Shell shell;
	private Text txtUnitCode;
	private static Table table;
	private static TableItem[] tblRow;
	private static Label lblOutput;
	private Spinner spinUnitMark;
	private static int maxTableRows = 30;
	private static int minTableRows = 12;
	private Text txtStudentId;
	private static EvaluatorInterface evaluate;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			EvaluatorClient window = new EvaluatorClient();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	private void open() {
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
	 * Initialise connection to eval server
	 */
	private boolean connectEval(){
		boolean connected = false;
		try {
			evaluate = (EvaluatorInterface)Naming.lookup("rmi://localhost/evaluate");
			connected = true;
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.out.println("Exception encountered during connectEval: " + e);
			lblOutput.setText("Exception encountered while initialising connecting to Evaluation server: " + e);
		}
		return connected;
	}
	
	/**
	 * Send unit codes & marks to RMI for eval
	 */
	private void sendMarks(){
		String studentId = txtStudentId.getText();
		if(studentId == null || studentId.equals("")){
			lblOutput.setText("Student Id cannot be empty. Enter a value and try again.");
			return;
		}
		
		if(tblRow != null){
			int numRows = tblRow.length;
			
			if(numRows>=12 && numRows <= 30){
				//Initialise connection
				boolean connected = connectEval();
				if(!connected){
					return;
				}
				
				//set Student Id
				try{
					evaluate.setStudentId(studentId);
				}catch(Exception e){
					lblOutput.setText("Encountered exception while transmitting Student Id: " + e);
				}
				System.out.println("Student Id set");
				
				/*
				 * Pack unit codes & marks
				 */
				String[][] usrMarks = new String[numRows][2];
				try{
					int count = 0;
					for(TableItem iRow : tblRow){
						//Get unit id & mark
						String unitId = iRow.getText(1);
						String unitMark = iRow.getText(2);
						
						//Pack unit id & mark
						usrMarks[count][0] = unitId; 
						usrMarks[count][1] = unitMark;
						
						//Increment counter
						count++;
					}					
				}catch(Exception e){
					System.out.println("Encountered error while packing marks: " + e);
					lblOutput.setText("Encountered error while packing marks: " + e);
				}
				System.out.println("Unit ID and marks packed");
				lblOutput.setText("Unit ID and marks packed");
				
				/*
				 * Send marks
				 */
				try{
					evaluate.setAllMarks(usrMarks);
				}catch(Exception e){
					System.out.println("Encountered error while sending marks: " + e);
					lblOutput.setText("Encountered error while sending marks: " + e);
				}
				
				/*
				 * Retrieve results & display
				 */
				try{
					String usrResults = evaluate.getEvalResults();
					
					//Display results
					lblOutput.setText(usrResults);
				}catch(Exception e){
					System.out.println("Encountered error while retrieving marks: " + e);
					lblOutput.setText("Encountered error while retrieving marks: " + e);
				}				
			}
			else{
				lblOutput.setText("Must submit between 12 and 30 entries, inclusive");
			}
		}
		else{
			lblOutput.setText("Minimum 12 entries");
		}
	}
	
	/**
	 * Add a row to the table
	 */
	private static void addRowTbl(String unitId, String unitMark){
		try {
			if(!(tblRow == null)){
				int numRows = tblRow.length;
				
				if(numRows < maxTableRows){
					TableItem[] tempArray = new TableItem[numRows+1];
					System.arraycopy(tblRow, 0, tempArray, 0, numRows);
					tempArray[numRows] = new TableItem(table, SWT.NONE);
					
					tempArray[numRows].setText(new String[]{Integer.toString(numRows+1), unitId, unitMark});
					tblRow = tempArray;
				}
				else{
					lblOutput.setText("Cannot exceed 30 entries");
				}
			}
			else{
				tblRow = new TableItem[1];			
				tblRow[0] = new TableItem(table, SWT.NONE);
				tblRow[0].setText(new String[]{ "1", unitId, unitMark});
			}
		} catch (Exception e) {
			lblOutput.setText("Error: " + e);
		}
	}
	
	/**
	 * Removes the last row in the table
	 */
	private static void popRowTbl(){		
		try{
			if(!(tblRow == null)){
				int numRows = tblRow.length;
				
				if(numRows != 0){
					tblRow[numRows-1].dispose();
					
					TableItem[] tempArray = new TableItem[numRows-1];
					System.arraycopy(tblRow, 0, tempArray, 0, (numRows-1));
					tblRow = tempArray;
				}
				else{
					lblOutput.setText("No entries to remove");
				}
			}
			else{
				lblOutput.setText("No entries to remove");
			}
		}catch(Exception e){
			lblOutput.setText("Error: " + e);
		}
		
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(451, 812);
		shell.setText("SWT Application");
		
		txtUnitCode = new Text(shell, SWT.BORDER);
		txtUnitCode.setText("e.g. CSI3106");
		txtUnitCode.setBounds(73, 37, 146, 21);
		
		Label lblUnitId = new Label(shell, SWT.NONE);
		lblUnitId.setBounds(12, 40, 55, 15);
		lblUnitId.setText("Unit ID:");
		
		Label lblUnitmark = new Label(shell, SWT.NONE);
		lblUnitmark.setBounds(12, 67, 55, 15);
		lblUnitmark.setText("UnitMark");
		
		Button btnAdd = new Button(shell, SWT.NONE);
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				String unitCode = txtUnitCode.getText();
				String unitMark = spinUnitMark.getText();
				if(unitCode.isEmpty()){
					lblOutput.setText("Unit code cannot be empty. Enter a value and submit again.");
				}
				else{
					addRowTbl(unitCode, unitMark);
				}
			}
		});
		btnAdd.setBounds(38, 102, 75, 25);
		btnAdd.setText("Add");
		
		Button btnRemove = new Button(shell, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				popRowTbl();
			}
		});
		btnRemove.setBounds(144, 102, 75, 25);
		btnRemove.setText("Remove");
		
		Button btnEvaluate = new Button(shell, SWT.NONE);
		btnEvaluate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				sendMarks();
			}
		});
		btnEvaluate.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		btnEvaluate.setFont(SWTResourceManager.getFont("Segoe UI", 20, SWT.BOLD));
		btnEvaluate.setBounds(257, 10, 167, 117);
		btnEvaluate.setText("Evaluate");
		
		spinUnitMark = new Spinner(shell, SWT.BORDER);
		spinUnitMark.setBounds(73, 64, 47, 22);
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 147, 414, 419);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnCount = new TableColumn(table, SWT.NONE);
		tblclmnCount.setWidth(35);
		tblclmnCount.setText("#");
		
		TableColumn tblclmnUnitId = new TableColumn(table, SWT.NONE);
		tblclmnUnitId.setWidth(180);
		tblclmnUnitId.setText("Unit ID");
		
		TableColumn tblclmnUnitMark = new TableColumn(table, SWT.NONE);
		tblclmnUnitMark.setWidth(196);
		tblclmnUnitMark.setText("Unit Mark");
		
		lblOutput = new Label(shell, SWT.BORDER | SWT.WRAP);
		lblOutput.setBounds(10, 591, 414, 172);
		lblOutput.setText("Outputs:");
		
		Label lblStudentId = new Label(shell, SWT.NONE);
		lblStudentId.setBounds(10, 13, 57, 15);
		lblStudentId.setText("Student Id:");
		
		txtStudentId = new Text(shell, SWT.BORDER);
		txtStudentId.setText("e.g. jf1236e0");
		txtStudentId.setBounds(73, 10, 146, 21);

	}
}
