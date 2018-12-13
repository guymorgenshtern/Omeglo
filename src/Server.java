import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    ServerSocket serverSock;// server socket for connection
    static Boolean running = true; // controls if the server is accepting clients
    ArrayList<ConnectionToClient> clientList = new ArrayList<>();
    ConnectionToClient tempConnection;

    /**
     * Main
     *
     * @param args parameters from command line
     */
    public static void main(String[] args) {
        new Server().go(); // start the server
    }

    /**
     * Go Starts the server
     */
    public void go() {
        System.out.println("Waiting for a client connection..");

        Socket client = null;// hold the client connection

        try {
            serverSock = new ServerSocket(5000); // assigns an port to the server
            while (running) { // this loops to accept multiple clients
                client = serverSock.accept(); // wait for connection
                System.out.println("Client connected");
                tempConnection = new ConnectionToClient(client);
                // Note: you might want to keep references to all clients if you plan to
                // broadcast messages
                // Also: Queues are good tools to buffer incoming/outgoing messages
                Thread t = new Thread(new ConnectionHandler(client, tempConnection)); // create a thread for the new client
                // and pass in the socket
                t.start(); // start the new thread
            }
        } catch (Exception e) {
            // System.out.println("Error accepting connection");
            // close all and quit
            try {
                client.close();
            } catch (Exception e1) {
                System.out.println("Failed to close socket");
            }
            System.exit(-1);
        }
    }

    // ***** Inner class - thread for client connection
    class ConnectionHandler implements Runnable {
        private PrintWriter output; // assign printwriter to network stream
        private BufferedReader input; // Stream for network input
        private Socket client; // keeps track of the client socket
        private ConnectionToClient clientConnection;
        private boolean running;
        String name = " ";
        boolean setName = false;
        int clientIndex;

        /*
         * ConnectionHandler Constructor
         *
         * @param the socket belonging to this client connection
         */
        ConnectionHandler(Socket s, ConnectionToClient connection) {
            this.client = s; // constructor assigns client to this
            this.clientIndex = clientIndex;
            this.clientConnection = connection;

            clientList.add(clientConnection);

            try { // assign all connections to client
                this.output = new PrintWriter(client.getOutputStream());
                InputStreamReader stream = new InputStreamReader(client.getInputStream());
                this.input = new BufferedReader(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            running = true;
        } // end of constructor

        /*
         * run executed on start of thread
         */
        public void run() {

            // Get a message from the client
            String msg = " ";
            String pmName = "";
            boolean userFound = false;
            boolean userTaken = false;
            boolean admin = false;
            String[] command;

            // Get a message from the client
            while (running) { // loop unit a message is received
                try {
                    if (input.ready()) { // check for an incoming messge
                        userFound = false;
                        msg = input.readLine(); // get a message from the client

                        if ((!(msg.startsWith("/"))) && (!(msg.startsWith("!")))) {
                            for (ConnectionToClient client : clientList) {
                                client.write(clientConnection.getName() + ": " + msg);
                            }
                        } else {
                            System.out.println("yeet");
                            command = findCommand(msg);
                            if (command[0].equals("/")) {
                                if (command[1].equals("name") ) {
                                    for (ConnectionToClient client: clientList) {
                                        if (name.equals(client.getName())) {
                                            userTaken = true;
                                        }
                                    }

                                    if (userTaken) {
                                        output.write("---Username Taken---");
                                    }

                                    if (!userTaken && !setName) {
                                        clientList.get(clientIndex).setName(command[2]);
                                        name = command[2];
                                        setName = true;
                                        updateStatusList();
                                        if (name.equals("admin")) {
                                            admin = true;
                                        }
                                        output.println("~Welcome To The Chat Server " + command[2] + "~");
                                        output.flush();
                                    }

                                } else if (command[1].equals("pm")) {
                                    for (ConnectionToClient client: clientList) {
                                        if (command[2].equals(client.getName())) {
                                            clientConnection.write("PM " + name + ": " + command[2]);
                                            client.write("PM " + name + ": " + command[2]);
                                            userFound = true;
                                        }
                                    }
                                    if (!userFound) {
                                        output.println("---Username not found---");
                                        output.flush();
                                    }
                                    System.out.println("Private Message");

                                }
                            } else if (command[0].equals("!")) { //admin commands
                                if (name.equals(admin)) {
                                    if (command.equals("kick")) {
                                        for (ConnectionToClient client: clientList) {
                                            if (client.getName().equals(command[2])) {
                                                client.write("---You Have Been Stopped---");
                                                client.socket.getInputStream().close();
                                                client.socket.getOutputStream().close();
                                                client.socket.close();
                                                clientList.remove(client);
                                            }
                                        }
                                    }
                                } else {
                                    output.write("---You Don't Have Permissions For This Command---");
                                }
                            } else {
                                System.out.println("msg from client: " + msg);
                                for (ConnectionToClient client : clientList) {
                                    client.write(clientConnection.getName() + ": " + msg);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Failed to receive msg from the client");
                    e.printStackTrace();
                }
            }

            // Send a message to the client

            // close the socket
//            try {
//                input.close();
//                output.close();
//                client.close();
//            }catch (Exception e) {
//                System.out.println("Failed to close socket");
//            }
        }

        public void updateStatusList() {
            String statusList =" ";
            for (int i = 0; i < clientList.size();i++) {
                for (ConnectionToClient client: clientList) {
                    statusList = "*" + statusList + client.getName();
                }
                clientList.get(i).write(statusList + "*");
            }
        }

        public String[] findCommand(String msg) {

            String[] commandVariables = new String[3];
            String indicator = "";
            String command = "";
            String parameter = "";

            if (msg.substring(0, 1).equals("/")) {
                indicator = "/";
            } else if (msg.startsWith("!")) {
                indicator = "!";
            }
            command = msg.substring(1, msg.indexOf(" "));

            String leftOverString = msg.substring(msg.indexOf(" "));
            parameter = leftOverString.substring(leftOverString.indexOf(" ") + 1);
            System.out.println(parameter);

            commandVariables[0] = indicator;
            commandVariables[1] = command;
            commandVariables[2] = parameter;

            return commandVariables;

        }
    } // end of inner class

    private class ConnectionToClient {
        Socket socket;
        InputStreamReader inputStream;
        PrintWriter outputStream;
        BufferedReader reader;
        String name;
        boolean admin;

        ConnectionToClient(Socket socket) {
            this.socket = socket;

            try {
                inputStream = new InputStreamReader(socket.getInputStream());
                outputStream = new PrintWriter(socket.getOutputStream());
                reader = new BufferedReader(inputStream);
            } catch (IOException e) {
            }
        }

        void write(String msg) {
            outputStream.println(msg);
            outputStream.flush();
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean getAdmin () {
            return admin;
        }

        public void setAdmin (boolean admin) {

        }
    }

}
