import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginPanel extends JPanel {
	
	private Window window;
	private JLayeredPane pane;
	private int x;
	private int y;
	Action action;
	
	private JTextField nameField;
	private JButton submitName;
	
	LoginPanel(Window window) {
		this.window = window;
		this.x = window.x;
		this.y = window.y;
		
		this.setLayout(new BorderLayout());
		pane = new JLayeredPane();
		pane.setBounds(100, 0, x-100, y);
		
		nameField = new JTextField(8);
		nameField.setBounds(x/2 - 180/2 + 100, 50, 180, 30);
		
		submitName = new JButton("Submit Name");
		submitName.setBounds(x/2 - 180/2 + 100, 90, 180, 30);
		action = new Action();
		submitName.addActionListener(action);

		pane.add(nameField);
		pane.add(submitName);

		add(pane);
		
		this.setVisible(true);

	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		repaint();
	}
	
	private class Action implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == submitName) {
				String name = nameField.getText();
				System.out.println(name);
				window.createAllChatPanel(name);
				window.changeState(2);
			}
		}
	}
}
