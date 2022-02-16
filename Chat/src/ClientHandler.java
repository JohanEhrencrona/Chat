import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Johan Ehrencrona joeh2789
 */
class ClientHandler extends Thread {
    private Server server;
    private Socket socket;


    /**
     * @param socket the clients socket
     * @param server the server which the client is connected to
     */
    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.start();
    }

    /**
     * Opens new thread and waits for client to send message which is
     * then broadcasted to all connected clients.
     * Disconnects the client if bufferedReader recives null or if any IOException is thrown
     */
    public void run() {
        try {
            String message;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            while((message = bufferedReader.readLine()) != null) {
                server.broadcast(this.socket, message);
                }
            bufferedReader.close();
        } catch (IOException e) {
            try {
                server.disconnectClient(socket);
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        try {
            server.disconnectClient(socket);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
