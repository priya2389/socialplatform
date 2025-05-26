// Package
package Interfaces;

public interface ServerMainMenuInterface extends Runnable {

    /**
     * Starts the server and begins accepting client connections
     * Handles each client in a separate thread using an ExecutorService
     */
    void run();

} // End of Interface