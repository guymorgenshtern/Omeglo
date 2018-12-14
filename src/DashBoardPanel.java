import java.awt.BorderLayout; 
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
 
public class DashBoardPanel extends JPanel {

	private Window window;
	private JLayeredPane pane;
	private int x;
	private int y;
	
	private JTextArea textArea;

	InputStreamReader inputStream;
	PrintWriter outputStream;
	BufferedReader reader;
	
	public ArrayList<String> onlineList = new ArrayList<String>();
	public ArrayList<JButton> buttonList = new ArrayList<JButton>();
	

	DashBoardPanel(Window window) {
				
		
		this.window = window;
		this.x = window.x;
		this.y = window.y;
		// setBackground(Color.red);
		this.setLayout(new BorderLayout());
		pane = new JLayeredPane();
		pane.setBounds(0, 0, 100, y);
		setSize(100, y);
		


		
//		textArea = new JTextArea();
//		textArea.setBounds(pane.getBounds().x + 10, 10, pane.getBounds().width - 10, pane.getBounds().height - 150);
//		textArea.setEditable(false);
//
//		pane.add(textArea);
		add(pane);
		
//		onlineList.add("NISNFAISA");
//		onlineList.add("NISNFAISA");
//		onlineList.add("NISNFAISA");
//		onlineList.add("NISNFAISA");
//		onlineList.add("NISNFAISA");
//		onlineList.add("NISNFAISA");
//		revalidateButtons();

		
		setOpaque(false);
		
		setDoubleBuffered(true);
		this.setVisible(true);

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

//		textArea.setText("");
//		for(int i = 0; i < onlineList.size(); i++) {
//			textArea.append(onlineList.get(i));
//			textArea.append("\n");
//		}
		
		
//		g.setColor(Color.blue);
//		g.fillRect(0, 0, 100, y);
//		for (int i = 0; i < onlineList.size(); i++) {
//			CustomButton b = new CustomButton(onlineList.get(i), 0, i*50, 100, 50);
//			b.draw(g, this);
//		}
		
		revalidateButtons();
		repaint();
	}
	
	
	public void revalidateButtons() {
		remove(pane);
		buttonList.clear();
		for (int i = 0; i < onlineList.size(); i++) {
			JButton b = new JButton(onlineList.get(i));
			b.setBounds(10, i*50 + 10, 80, 30);
			Action a = new Action(onlineList.get(i));
			b.addActionListener(a);
			pane.add(b);
		}
		add(pane);
		
	}

	public void addClients(String msg) {		
		if (msg.length() < 2) {
			return;
		}
		if (msg.startsWith("*")) {
			String sub = msg.substring(1);
			String name = sub.substring(0, sub.indexOf("*"));
			onlineList.add(name);
			addClients(sub.substring(sub.indexOf("*")));
		} else {
			return;
		}
	}
	
	public ArrayList<String> getOnlineList() {
		return onlineList;
	}
	
	private class Action implements ActionListener {
		
		String text;
		
		Action(String text) {
			this.text = text;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			

			window.getAllChatPanel().setTextFieldText("/pm " + text + " ");

			

		}
	}
	

}
