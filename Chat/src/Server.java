import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Johan Ehrencrona joeh2789
 */
public class Server extends JFrame implements Runnable {
    private ServerSocket serverSocket;
    private static int port = 2000;
    private JTextArea textArea = new JTextArea();
    private Thread thread = new Thread(this);
    private boolean active = true;
    private List<Socket> clients = Collections.synchronizedList(new ArrayList<>());

    /**
     * Opens thread and runs while boolean active is true
     * Holds thread until a client connects
     */
    @Override
    public void run() {
        while (active) {
            Socket socket = null;
            try {
                socket = this.serverSocket.accept();
                addClient(socket);
                setTitle("HOST: " + getServerHost() + " PORT: " + port + " CLIENTS: " + clients.size());
            } catch (IOException e) {
                System.out.println("Error when loading client");
            }
        }
    }

    /**
     * @param port port for serversocket
     * @throws UnknownHostException thrown if host cant be found by getServerHost()
     */
    public Server(int port) throws UnknownHostException {
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error when connecting to port");
        }
        getContentPane().add("Center", new JScrollPane(this.textArea));
        setSize(600, 300);
        setTitle("HOST: " + getServerHost() + " PORT: " + port + " CLIENTS: " + clients.size());

        WindowAdapter onClose = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                CloseAll();
            }
        };
        addWindowListener(onClose);


        setVisible(true);
        thread.start();
    }

    /**
     * @param socket the clients socket
     * Adds socket to synchronized array list
     * Sends messages to all clients that a new client has connected
     */
    private synchronized void addClient(Socket socket) {
        new ClientHandler(socket, this);
        clients.add(socket);
        broadcast(socket, " CONNECTED");
    }

    /**
     * @param socket the clients socket which is to be disconnected
     * @throws UnknownHostException thrown if host cant be found by getServerHost()
     * Removes the socket from the arraylist Clients and broadcasts that the client is disconnected
     */
    public synchronized void disconnectClient(Socket socket) throws UnknownHostException {
        clients.remove(socket);
        broadcast(socket, getClientHost(socket) + " DISCONNECTED");
        setTitle("HOST: " + getServerHost() + " PORT: " + port + " CLIENTS: " + clients.size());
    }


    /**
     * @param socket the socket of the client which is sending a message
     * @param message the intended message to be sent
     * Iterates over list of clients and sends the message
     * individually by printing to their outputstream via their socket.
     */
    public synchronized void broadcast(Socket socket, String message) {
        textArea.append("CLIENT: " + getClientHost(socket) + " MESSAGE: " + message + "\n");
        for (Socket client : clients){
            try{
                PrintWriter printWriter = new PrintWriter(client.getOutputStream(), true);
                printWriter.println(getClientHost(socket) + ": " + message);
            }catch(IOException e){
                System.out.println("Error when sending message from " + getClientHost(socket) + " to " + getClientHost(client));
            }
        }
    }

    /**
     * @return the IP address of the socket
     * @throws UnknownHostException if the host cant be found
     */
    private String getServerHost() throws UnknownHostException {
        return serverSocket.getInetAddress().getLocalHost().getHostName();
    }

    /**
     * @return the IP address of the socket
     * @throws UnknownHostException if the host cant be found
     */
    private String getClientHost(Socket client){
        return client.getInetAddress().getHostName();
    }

    /**
     * Closes the window, serversocket and thread.
     */
    private void CloseAll(){
        try{
            this.serverSocket.close();
        }catch (IOException e){
            System.out.println("IO Exception");
        }
        this.active = false;
        this.dispose();
        System.exit(1);
    }

    /**
     * Starts the program and creates a server with the port 2000 if nothing else is entered
     * @param input the port which to be entered.
     * @throws Exception if there are more than one argument entered.
     */
    public static void main(String[] input) throws Exception {
        if(input.length == 0){
            new Server(2000);
        }else if(input.length == 1){
            new Server(Integer.parseInt(input[0]));
        }else throw new Exception("Error: Too many arguments");
    }
}