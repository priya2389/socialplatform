// Package
package Interfaces;

public interface GuiInterface {

    /**
     * Initializes the GUI with the main Jframe, and also calls constructors to the various initial cards that the user
     * will see
     */
    // void initializeGUI();

    /**
     * Initializes the welcome panel card (What the user sees upon first boot)
     */
    // getWelcomePanel();

    /**
     * Initializes the login panel card
     */
    // getLoginPanel();

    /**
     * Initializes the registration panel card
     */
    // getRegisterPanel();

    /**
     * Initializes the main menu panel card (What user sees after successful login)
     */
    // getUserMenuPanel();

    /**
     * Creates a new account with user-provided information (name, username, password)
     */
    // void createAccount();

    /**
     * Logs in the user using their provided username and password
     */
    // void login();

    /**
     * Displays the users main profile page and information, along with their image. In this pane, users can add/remove
     * a pfp, change their username, and change their password
     */
    // void viewProfile();

    /**
     * Displays list of pending friend requests
     */
    // void viewFriendRequests();

    /**
     * Blocks/unblocks a user by their username
     */
    // void blockUnBlockUser();

    /**
     * Creates a new post with user-provided content
     */
    // void createPost();

    /**
     * Displays the user's feed with posts and their respective interactions
     */
    // void viewFeed();

    /**
     * From the post view, allows the user to see the comments under the current post
     */
    // void viewComments();

    /**
     * GUI interface for actually displaying the posts, which is called upon by viewFeed();
     */
    // void displayPosts()

    /**
     * Deletes the user's account
     */
    // void deleteAccount();

    /**
     * Deletes a post by its ID
     */
    // void deletePost(Scanner scanner);

    /**
     * Searches for users by username, and also gives the option to block the user, or add them as a friend
     */
    // void searchUsers(Scanner scanner);

    /**
     * Logs the user out of the application
     */
    // void logout();

    /**
     * Hides a post based on either username or a specific post ID
     */
    // void hidePost();


    /**
     * Shows file input to upload a pfp, and sets the users pfp accordingly
     */
    // void uploadProfilePicure();

    /**
     * Sends a request to the server
     */
    // void sendRequest(String request);

    /**
     * Exits the client application which closes the connection to the server
     */
    // void quit();
}