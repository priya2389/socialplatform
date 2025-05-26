// Package
package MainClasses;

// Imports
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import Interfaces.ServerMainMenuInterface;

public class ServerMainMenu implements Runnable, ServerMainMenuInterface {
    private static final int PORT = 12345; // You can choose any available port
    private ServerSocket serverSocket;
    private UserManager userManager;
    private FriendRequestSystem friendRequestSystem;
    private UserFeed userFeed;
    private ExecutorService threadPool;

    public ServerMainMenu() {
        try {
            serverSocket = new ServerSocket(PORT);
            userManager = new UserManager();
            friendRequestSystem = new FriendRequestSystem(userManager);
            userFeed = new UserFeed(friendRequestSystem);
            threadPool = Executors.newCachedThreadPool();
            System.out.println("Server started on port " + PORT);
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Handle each client in a new thread
                ClientHandler clientHandler = new ClientHandler(clientSocket, userManager, friendRequestSystem,
                        userFeed);
                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.out.println("Error accepting client connections: " + e.getMessage());
        } finally {
            threadPool.shutdown();
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing server socket: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        ServerMainMenu server = new ServerMainMenu();
        new Thread(server).start();
    }
}