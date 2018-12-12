import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    String name;
    boolean setName = false;
    JButton sendButton, clearButton;
    JTextField typeField;
    JTextArea msgArea;
    JPanel southPanel;
    InputStreamReader inputStream;
    PrintWriter outputStream;
    BufferedReader reader;

    public static void main(String[] args) {
        new Client().go();
    }

    public void go() {


        String msg = " ";
        JFrame window = new JFrame("Chat Client");
        Action action = new Action();


        southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(2, 0));

        sendButton = new JButton("SEND");
        clearButton = new JButton("CLEAR");

        sendButton.addActionListener(action);
        clearButton.addActionListener(action);

        JLabel errorLabel = new JLabel("");

        typeField = new JTextField(10);

        msgArea = new JTextArea();

        southPanel.add(typeField);
        southPanel.add(sendButton);
        southPanel.add(errorLabel);
        southPanel.add(clearButton);

        window.add(BorderLayout.CENTER, msgArea);
        window.add(BorderLayout.SOUTH, southPanel);

        window.setSize(400, 400);
        window.setVisible(true);

        // call a method that connects to the server
        connectToServer();
        // after connecting loop and keep appending[.append()] to the JTextArea


        msgArea.append("Please Set Your Username Using /name ___");
        while(true) {
        try {
            msg = reader.readLine();
        } catch (IOException e) {
        }
                msgArea.append("\n");
                msgArea.append(msg);

            }
    }

    public void connectToServer() {
        try {
            Socket mySocket = new Socket("127.0.0.1", 5000);
            inputStream = new InputStreamReader(mySocket.getInputStream());
            outputStream = new PrintWriter(mySocket.getOutputStream());
            reader = new BufferedReader(inputStream);
        } catch (IOException e) {
            System.out.println("Failed To Connect To Server");
        }
    }

    //****** Inner Classes for Action Listeners ****

    //To complete this you will need to add action listeners to both buttons
    // clear - clears the textfield
    // send - send msg to server (also flush), then clear the JTextField

    private class Action implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == sendButton) {
                    outputStream.println(typeField.getText());
                    outputStream.flush();
                    typeField.setText(" ");
            }

            if (e.getSource() == clearButton) {
                typeField.setText(" ");
            }
        }
    }
}
