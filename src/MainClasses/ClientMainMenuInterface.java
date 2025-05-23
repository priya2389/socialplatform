// Package
package MainClasses;

public interface ClientMainMenuInterface {
    /**
     * Starts client application and displays the main menu for user interaction. Handles user interaction with ALL
     * other methods
     */
    void start();

    /**
     * Creates a new account with user-provided information (name, username, password)
     */
    // void createAccount(Scanner scanner);

    /**
     * Logs in the user using their provided username and password
     */
    // void login(Scanner scanner);

    /**
     * Displays user menu with various options such as viewing profile, adding friends, and creating posts
     */
    // void userMenu(Scanner scanner);

    /**
     * Displays the user profile information
     */
    // void viewProfile();

    /**
     * Sends a request to add a friend by username
     */
    // void addFriend(Scanner scanner);

    /**
     * Displays list of pending friend requests
     */
    // void viewFriendRequests();

    /**
     * Blocks a user by their username
     */
    // void blockUser(Scanner scanner);

    /**
     * Unblocks a previously blocked user by their username
     */
    // void unblockUser(Scanner scanner);

    /**
     * Creates a new post with user-provided content
     */
    // void createPost(Scanner scanner);

    /**
     * Displays the user's feed with posts and their respective interactions
     */
    // void viewFeed(Scanner scanner);

    /**
     * Deletes the user's account
     */
    // void deleteAccount();

    /**
     * Deletes a post by its ID
     */
    // void deletePost(Scanner scanner);

    /**
     * Searches for users by username
     */
    // void searchUsers(Scanner scanner);

    /**
     * Logs the user out of the application
     */
    // void logout();

    /**
     * Sends a request to the server
     */
    // void sendRequest(String request);

    /**
     * Exits the client application which closes the connection to the server
     */
    // void quit();

} // End of Interface