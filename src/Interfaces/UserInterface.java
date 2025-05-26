// Package
package Interfaces;

// Imports
import java.util.*;

import MainClasses.UserManager;

public interface UserInterface {

    /**
     * Gets ID of the user
     * @return the user's ID
     */
    String getId();

    /**
     * Gets name of the user
     * @return the user's name
     */
    String getName();

    /**
     * Gets username of the user
     * @return the user's username
     */
    String getUsername();

    /**
     * Gets password of the user
     * @return the user's password
     */
    String getPassword();

    /**
     * Gets list of the user's friends
     * @return a set of user IDs representing the user's friends
     */
    Set<String> getFriendList();

    /**
     * Gets list of users that the user has blocked
     * @return a set of user IDs representing the users that are blocked
     */
    Set<String> getBlockedList();

    /**
     * Changes username of the user
     */
    boolean changeUsername(String newUsername, UserManager userManager);

    /**
     * Changes password of the user
     */
    void changePassword(String newPassword);

    /**
     * Saves user's data (ID, name, username, password, friends, and blocked list) to a file
     */
    void saveUserToFile();

    /**
     * Loads user's data (ID, name, username, password, friends, and blocked list) from a file
     */
    // void loadUserFromFile();
}