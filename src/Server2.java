import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server2 {

    ServerSocket serverSock;// server socket for connection
    static Boolean running = true; // controls if the server is accepting clients
    ArrayList<ConnectionToClient> clientList = new ArrayList<>();
    ArrayList<ConnectionToClient> banList = new ArrayList<>();
    ConnectionToClient tempConnection;
    boolean isBanned = false;

    /**
     * Main
     *
     * @param args parameters from command line
     */
    public static void main(String[] args) {
        new Server2().go(); // start the server
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
                for (ConnectionToClient clientCheck: banList) {
                    if (tempConnection.getSocket().getRemoteSocketAddress().toString().equals
                            (clientCheck.getSocket().getRemoteSocketAddress().toString())) {
                        tempConnection.write("You are a banned user");
                        System.out.println("banned");
                        isBanned = true;
                    }
                }
                if (!isBanned) {
                    Thread t = new Thread(new ConnectionHandler(client, tempConnection)); // create a thread for the new client
                    // and pass in the socket
                    t.start(); // start the new thread
                }
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
        String name = "";
        boolean setName = false;

        /*
         * ConnectionHandler Constructor
         *
         * @param the socket belonging to this client connection
         */
        ConnectionHandler(Socket s, ConnectionToClient connection) {
            this.client = s; // constructor assigns client to this
            this.clientConnection = connection;

            try { // assign all connections to client
                this.output = new PrintWriter(client.getOutputStream());
                InputStreamReader stream = new InputStreamReader(client.getInputStream()); //get clients input stream
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
            String msg = " ";
            boolean userFound = false;
            boolean userTaken = false;
            boolean admin = false;
            String[] command;
            String param = " ";

            // Get a message from the client
            while (running) { // loop unit a message is received
                try {
                    if (input.ready()) { // check for an incoming messge
                        userFound = false;
                        msg = input.readLine(); // get a message from the client
                        if ((!msg.startsWith("/")) && (!msg.startsWith("!"))) { //command indicators
                            for (ConnectionToClient client : clientList) {
                                client.write(clientConnection.getName() + ": " + msg);
                            }
                        } else {
                            command = findCommand(msg);
                            if (command[0].equals("/")) {
                                System.out.println(command[1] + "yeey");
                                if (command[1].equals("name") ) {
                                	
                                	param = command[2];
                                	
                                    for (ConnectionToClient client: clientList) {
                                        if (param.equals(client.getName())) {
                                            userTaken = true;
                                        }
                                    }

                                    if (userTaken) {
                                        output.write("---Username Taken---");
                                    }

                                    if (!userTaken && !setName) {
                                        clientConnection.setName(param);
                                        name = param;
                                        setName = true;
                                        clientList.add(clientConnection);
                                        updateStatusList();
                                        if (name.equals("admin")) {
                                            clientConnection.setAdmin(true);
                                        }

                                        output.println("~Welcome To The Chat Server " + name + "~");
                                        output.flush();
                                    }

                                } else if (command[1].equals("pm")) {
                                    for (ConnectionToClient client: clientList) {
                                        System.out.println(command[2] + " " + client.getName());
                                        if (command[2].equals(client.getName())) {
                                            client.write("PM From " + clientConnection.getName() + ": " + command[3]);
                                            clientConnection.write("PM To " + client.getName() + ": " + command[3]);
                                            userFound = true;
                                        }
                                    }
                                    if (!userFound) {
                                        output.println("---Username not found---");
                                        output.flush();
                                    }
                                    System.out.println("Private Message");

                                } else if (command[1].equals("help")){
                                    output.println("/pm (name) - This will send a private message to the user");
                                    output.println("/help - Displays a list of commands");
                                    output.println("/clear - Clears the current chat");
                                    output.print("/quit - quits the server");
                                    output.flush();
                                } else if (command[1].equals("quit")) {
                                    output.println("Quitting");
                                    output.flush();
                                    clientConnection.inputStream.close();
                                    clientConnection.outputStream.close();
                                    clientList.remove(clientConnection);
                                }
                            } else if (command[0].equals("!")) { //admin commands
                                ConnectionToClient buffer = null;
                                boolean kickFound = false;
                                System.out.println(clientConnection.getName() + " " + clientConnection.getAdmin());
                                if (clientConnection.getAdmin()) {
                                    if (command[1].equals("kick")) {
                                            for (ConnectionToClient client : clientList) {
                                                if (client.getName().equals(command[2])) {
                                                    client.write("---You Have Been Stopped---");
                                                    buffer = client;
                                                    kickFound = true;
                                                }

                                            }
                                        if (kickFound) {
                                            clientList.remove(buffer);
                                            updateStatusList();
                                        }

                                    } else if (command[1].equals("ban")) {
                                        for (ConnectionToClient client: clientList) {
                                            if (client.getName().equals(command[2])) {
                                                System.out.println(client.getSocket().getRemoteSocketAddress().toString());
                                                client.write("~ban");
                                                banList.add(client);
                                                client.inputStream.close();
                                                client.outputStream.close();
                                                clientList.remove(client);
                                            }
                                        }
                                    } else if (command[1].equals("help")) {
                                        output.println("!kick (name) - Kicks the specified user from the server");
                                        output.println("!ban (name) - Bans the IP of the specified user");
                                        output.flush();
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

        }

        public void updateStatusList() {
            for (int i = 0; i < clientList.size();i++) {
            	String longString = "";
                for (ConnectionToClient client: clientList) {
                        longString += "*" + client.getName();
                }
                longString += "*";
                System.out.println(longString);
                clientList.get(i).write(longString);
            }
        }

        public String[] findCommand(String msg) {

            String[] commandVariables = new String[4];
            String indicator = "";
            String command = "";
            String parameter = "";
            String message = "";

            if (msg.substring(0, 1).equals("/")) {
                indicator = "/";
            } else if (msg.startsWith("!")) {
                indicator = "!";
            }
            command = msg.substring(1);
            try {
                command = msg.substring(1, msg.indexOf(" "));

                String leftOverString = msg.substring(msg.indexOf(" "));
                parameter = leftOverString.substring(leftOverString.indexOf(" ") + 1);


                    parameter = leftOverString.substring(leftOverString.indexOf(" ") + 1,
                            leftOverString.indexOf(" ", leftOverString.indexOf(" ") + 1));
                    leftOverString = leftOverString.substring(leftOverString.indexOf(" ", leftOverString.indexOf(" ") + 1) + 1);
                    message = leftOverString;
            } catch (StringIndexOutOfBoundsException e) {
            }

            commandVariables[0] = indicator;
            commandVariables[1] = command;
            commandVariables[2] = parameter;
            commandVariables[3] = message;

            return commandVariables;
        }
    } // end of inner class

    private class ConnectionToClient {

        private Socket socket;
        private InputStreamReader inputStream;
        private PrintWriter outputStream;
        private BufferedReader reader;
        private String name;
        private boolean admin;

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
            this.admin = admin;
        }

        public Socket getSocket() {
            return socket;
        }
     }



}