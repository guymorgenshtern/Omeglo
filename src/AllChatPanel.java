import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AllChatPanel extends JPanel {

	private Window window;
	private JLayeredPane pane;
	private int x;
	private int y;



	JTextArea textArea;
	JButton send;
	JTextField textField;
	Action action;
	
	
	String username = "Anon";

	AllChatPanel(Window window, String username) {
		this.window = window;
		this.x = window.x;
		this.y = window.y;
		this.username = username;
		// setBackground(Color.red);
		this.setLayout(new BorderLayout());
		pane = new JLayeredPane();
		pane.setBounds(10, 0, x - 10, y);

		textArea = new JTextArea();
		textArea.setBounds(10, 10, pane.getBounds().width - 110, pane.getBounds().height - 150);
		textArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(10, 10, pane.getBounds().width - 110, pane.getBounds().height - 150);
		
		
		send = new JButton("Send");
		send.setBounds(x - 190, y - 120, 80, 30);
		action = new Action();
		send.addActionListener(action);

		textField = new JTextField(8);
		textField.setBounds(10, send.getBounds().y, send.getBounds().x - 20, 30);
		textField.addKeyListener(new myKeyListener());

		pane.add(scroll);
		pane.add(send);
		pane.add(textField);

		add(pane);

		this.setVisible(true);
		
		

		

	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);


		textField.requestFocus();
		repaint();
	}

	public void write(String msg) {
		textArea.append("\n");
		textArea.append(msg);
	}

	private class Action implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == send) {
				
				window.sendAll(textField.getText());
			
				textField.setText("");
			}
		}
	}
	
	private class myKeyListener implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				window.sendAll(textField.getText());
				textField.setText("");
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	public void setTextFieldText(String txt) {
		textField.setText(txt);
	}
}
