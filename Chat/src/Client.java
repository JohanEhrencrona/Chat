import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;


/**
 * @author Johan Ehrencrona joeh2789
 * @version 1.0
 */

public class Client extends JFrame implements Runnable{
    private static String host;
    private static int port;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private JTextField textField = new JTextField();
    private JTextArea textArea = new JTextArea();
    private Thread thread = new Thread(this);
    private boolean active = true;

    /**
     * Constructor for client class. Instantiates thread, socket, PrintWriter (sending messages) and BufferedReader (receiving messages.
     * @param host the IP to connect to
     * @param port the port on the requested IP
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;

        getContentPane().add("South", textField);
        getContentPane().add("Center", new JScrollPane(this.textArea));
        setSize(600, 300);
        setTitle("Chat Client");

        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(textField.getText());
            }
        };
        textField.addActionListener(action);

        WindowAdapter onClose = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                CloseAll();
            }
        };
        addWindowListener(onClose);

        setVisible(true);
        
        try {
            this.socket = new Socket(this.host, this.port);
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.printWriter = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(), "ISO-8859-1"), true);
            this.thread.start();
            this.setTitle("CONNECTED -   HOST: " + host + " PORT: "+ port);
        } catch (IOException e) {
            System.out.println("IO Exception");
            CloseAll();
        }

    }


    /**
     * While loop for recieving messages
     */
    @Override
    public void run(){
        String message = "";
        while(active){
            try{
                message = bufferedReader.readLine();
                textArea.append(message + "\n");
            }catch (IOException e){
                System.out.println("IO Exception");
                CloseAll();
            }
        }
    }

    /**
     * Sends messages via PrintWriter
     * @param message the message which is to be sent
     */
    private void send(String message){
        try {
            this.printWriter.println(message);
            this.textField.setText("");
        }catch(NumberFormatException e){
            System.out.println("IO Exception");
            this.CloseAll();
        }
    }

    /**
     * Closes all open I/O, socket, threads and windows.
     */
    private void CloseAll(){
        try{
            this.printWriter.close();
            this.bufferedReader.close();
            this.socket.close();
        }catch (IOException e){
            System.out.println("IO Exception");
        }
        this.active = false;
        this.dispose();
        System.exit(1);
    }


}


