import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    ServerSocket serverSock;// server socket for connection
    static Boolean running = true;  // controls if the server is accepting clients
    ArrayList<ConnectionToClient> clientList = new ArrayList<>();
    int clientIndex;

    /** Main
     * @param args parameters from command line
     */
    public static void main(String[] args) {
        new Server().go(); //start the server
    }

    /** Go
     * Starts the server
     */
    public void go() {
        System.out.println("Waiting for a client connection..");

        Socket client = null;//hold the client connection

        try {
            serverSock = new ServerSocket(5000);  //assigns an port to the server
            while(running) {  //this loops to accept multiple clients
                client = serverSock.accept();  //wait for connection
                System.out.println("Client connected");
                clientList.add(new ConnectionToClient(client));
                clientIndex++;
                //Note: you might want to keep references to all clients if you plan to broadcast messages
                //Also: Queues are good tools to buffer incoming/outgoing messages
                Thread t = new Thread(new ConnectionHandler(client, clientIndex)); //create a thread for the new client and pass in the socket
                t.start(); //start the new thread
            }
        }catch(Exception e) {
            // System.out.println("Error accepting connection");
            //close all and quit
            try {
                client.close();
            }catch (Exception e1) {
                System.out.println("Failed to close socket");
            }
            System.exit(-1);
        }
    }

    //***** Inner class - thread for client connection
    class ConnectionHandler implements Runnable {
        private PrintWriter output; //assign printwriter to network stream
        private BufferedReader input; //Stream for network input
        private Socket client;  //keeps track of the client socket
        private boolean running;
        String name = " ";
        boolean setName = false;
        int clientIndex;
        /* ConnectionHandler
         * Constructor
         * @param the socket belonging to this client connection
         */
        ConnectionHandler(Socket s, int clientIndex) {
            this.client = s;  //constructor assigns client to this
            this.clientIndex = clientIndex;
            try {  //assign all connections to client
                this.output = new PrintWriter(client.getOutputStream());
                InputStreamReader stream = new InputStreamReader(client.getInputStream());
                this.input = new BufferedReader(stream);
            }catch(IOException e) {
                e.printStackTrace();
            }
            running=true;
        } //end of constructor


        /* run
         * executed on start of thread
         */
        public void run() {

            //Get a message from the client
            String msg= " ";
            String pmName = " ";

            //Get a message from the client
            while(running) {  // loop unit a message is received
                try {
                    if (input.ready()) { //check for an incoming messge
                        msg = input.readLine();  //get a message from the client
                        System.out.println(msg);
                        if (msg.substring(0, 1).equals("/")) {
                            if (msg.substring(1, 5).equals("name") && setName == false) {
                                name = msg.substring(6);
                                System.out.println(name);
                                clientList.get(clientIndex).setName(name);
                                setName = true;
                                output.println("~Welcome To The Chat Server " + name + "~" );
                                output.flush();
                            } else if (msg.substring(1,3).equals("pm")) {
                                for (int i = 0; i < )
                                for (ConnectionToClient client : clientList) {
                                    if (client.getName().equals(msg))
                                }
                            }
                        } else {
                            System.out.println("msg from client: " + msg);
                            for (ConnectionToClient client : clientList) {
                                client.write(name + ": " + msg);
                            }
                        }
                    }
                }catch (IOException e) {
                    System.out.println("Failed to receive msg from the client");
                    e.printStackTrace();
                }
            }

            //Send a message to the client


            //close the socket
//            try {
//                input.close();
//                output.close();
//                client.close();
//            }catch (Exception e) {
//                System.out.println("Failed to close socket");
//            }
        } // end of run()
    } //end of inner class

    private class ConnectionToClient {
        Socket socket;
        InputStreamReader inputStream;
        PrintWriter outputStream;
        BufferedReader reader;
        String name;
        ConnectionToClient(Socket socket) {
            this.socket = socket;

            try {
                inputStream = new InputStreamReader(socket.getInputStream());
                outputStream = new PrintWriter(socket.getOutputStream());
                reader = new BufferedReader(inputStream);
            } catch (IOException e){
            }
        }

        void write (String msg) {
            outputStream.println(msg);
            outputStream.flush();
        }

        public void setName (String name) {
            this.name = name;
        }

        public String getName (){
            return name;
        }
    }

}
