

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Main display hub for the program
 * All panels stem from this one
 */
public class Window extends JFrame {
    public int x;
    public int y;

    private JPanel loginPanel;
    private JPanel pmPanel;
    private AllChatPanel allChatPanel;
    private DashBoardPanel dashboardPanel;
    String msg;
    
    private Socket mySocket = null;
    
	InputStreamReader inputStream;
	PrintWriter outputStream;
	BufferedReader reader;
	JFrame dashboardFrame;
    
    public Window(int x, int y) {
        super("Chat++");
        
		try {
			mySocket = new Socket("127.0.0.1", 5000);
		} catch (IOException e1) {
			e1.printStackTrace();
		}


        //generate frame
        this.x = x;
        this.y = y;
        this.setLocation(100, 0);
        this.setSize(new Dimension(x, y));
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        
        /* -- INCOMING PATCH UPDATE -- FRIEND DASHBOARD */
        dashboardFrame = new JFrame("Online");
        dashboardFrame.setBounds(0, 0, 100, 400);
        dashboardFrame.setSize(100, 0);
        dashboardFrame.setResizable(false);
        dashboardFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
        this.dashboardPanel = new DashBoardPanel(this);
        dashboardFrame.getContentPane().add(dashboardPanel);
        dashboardFrame.setVisible(true);
        dashboardFrame.setAlwaysOnTop(true);
        
        
        
        //generate panels
        this.loginPanel = new LoginPanel(this);
        this.pmPanel = new PMPanel(this);


        //set displayed panel to menu
        changeState(0);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        this.setVisible(true);
    }
    
    public void createAllChatPanel(String username) {
        this.allChatPanel = new AllChatPanel(this, username);
        
		Thread clientThread = new Thread(new Runnable() {

	        @Override
	        public void run() {
	    		connectToServer();

	    		outputStream.println("/name " + username);
	    		outputStream.flush();
				
	        	while(true) {
	        		
	        		dashboardFrame.setSize(100, dashboardPanel.getOnlineList().size()*50 + 25);
	        		
		    		try {
		    			msg = reader.readLine();
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}
		    		
		    		
//		    		msg = "*guy*yash*katelyn*alston*";
		    		
		    		// if * then its a client name
		    		if (msg.startsWith("*")) {
		    			dashboardPanel.getOnlineList().clear();
		    			dashboardPanel.addClients(msg);
		    		} else {
			    		allChatPanel.write(msg);
		    		}
		    		

	        	}
	        	

	        }
		});


		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	clientThread.start();
    }
    
    public void sendAll(String msg) {
		outputStream.println(msg);
		outputStream.flush();
    }
    
	public void connectToServer() {
		try {
			Socket mySocket = getSocket();
			inputStream = new InputStreamReader(mySocket.getInputStream());
			outputStream = new PrintWriter(mySocket.getOutputStream());
			reader = new BufferedReader(inputStream);
			
		} catch (IOException e) {
			System.out.println("Failed To Connect To Server");
		}
	}
    
    public Socket getSocket() {
    	return this.mySocket;
    }

    /**
     * 0 = login, 1 = pm, 2 = allChat
     */
    public void changeState(int state) {
        switch (state) {
            case 0:
            	switchPanel(loginPanel);
                return;
            case 1:
            	switchPanel(pmPanel);
            	return;
            case 2:
                switchPanel(allChatPanel);
            	return;
            default:
                throw new IndexOutOfBoundsException();
        }
    }


    private void closeWindow() {
        dispose();
    }

    private void switchPanel(JPanel newPanel) {
        getContentPane().removeAll();
        getContentPane().add(newPanel); 
        
        getContentPane().revalidate();
        getContentPane().repaint(); 
    }
    
}
