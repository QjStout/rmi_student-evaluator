package login;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.rmi.Naming;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class LoginClient {

	protected Shell shell;
	private Text txtUsername;
	private Text txtPassword;
	private Label lblMsgOut;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {		
		try {
			LoginClient window = new LoginClient();
			window.open();
		} catch (Exception e1) {
			e1.printStackTrace();
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
	
	public void close(){
		this.shell.setVisible(false);
		this.shell.dispose();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		
		Label lblUsername = new Label(shell, SWT.NONE);
		lblUsername.setBounds(73, 30, 55, 15);
		lblUsername.setText("Username");
		
		Label lblPassword = new Label(shell, SWT.NONE);
		lblPassword.setText("Password");
		lblPassword.setBounds(73, 78, 55, 15);
		
		txtUsername = new Text(shell, SWT.BORDER);
		txtUsername.setText("lisa");
		txtUsername.setBounds(139, 24, 209, 21);
		
		txtPassword = new Text(shell, SWT.BORDER);
		txtPassword.setText("password1");
		txtPassword.setBounds(139, 72, 209, 21);
		
		Button btnLogin = new Button(shell, SWT.NONE);
		btnLogin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				String usrname = txtUsername.getText();
				String usrpass = txtPassword.getText();
				
				// Call authenticate user credentials through authentication RMI server
				LoginInterface login;
				try{
					login = (LoginInterface)Naming.lookup("rmi://localhost/login");
					
					boolean authenticated = login.authenticate(usrname, usrpass);
					if(authenticated){
						System.out.println("User authenticated.");
						close();

						evaluator.EvaluatorClient.main(null);
					}
					else{
						System.out.println("Username or password was incorrect. Please try again.");
						lblMsgOut.setText("Username or password was incorrect. Please try again.");
					}
						
				}catch(Exception e2){
					System.out.println("LoginClient authentication exception: " + e2);
					lblMsgOut.setText("LoginClient authentication exception: " + e2);
				}
			}
		});
		btnLogin.setBounds(272, 118, 75, 25);
		btnLogin.setText("Login");
		
		lblMsgOut = new Label(shell, SWT.BORDER | SWT.WRAP);
		lblMsgOut.setBounds(10, 174, 414, 77);
		lblMsgOut.setText("Output:");

	}
}
