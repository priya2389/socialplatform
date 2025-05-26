// Package
package Interfaces;

// Imports
import java.util.List;

public interface UserManagerInterface {

    /**
     * Retrieves a list of all usernames
     *
     * @return List of all usernames
     */
    List<String> getAllUsernames();

    /**
     * Checks if a user with the specified username exists
     *
     * @return true if the username exists, false if not
     */
    boolean userExists(String username);

    /**
     * Checks if a user with the specified userId exists
     *
     * @return true if the userId exists, false if not
     */
    boolean userIdExists(String userId);

    /**
     * Registers a new user with the given information.
     *
     * @return true if the registration is successful, false if not
     */
    boolean registerUser(String name, String username, String password);

    /**
     * Logs in a user by verifying the username and password
     *
     * @return true if the login is successful, false if not
     */
    boolean loginUser(String username, String password);

    /**
     * Retrieves the userId associated with a given username
     *
     * @return the userId associated with the username
     */
    String getUserIdByUsername(String username);

    /**
     * Retrieves the username associated with a given userId
     *
     * @return the username associated with the userId
     */
    String getUsernameById(String userId);

    /**
     * Validates the format of a password according to certain criteria.
     *
     * @return true if the password meets the criteria, false if it doesn't
     */
    boolean isValidPassword(String password);

    /**
     * Deletes a user by username
     *
     * @return true if the deletion is successful, false it isn't
     */
    boolean deleteUser(String username);

    /**
     * Removes a mapping of the specified username
     */
    void removeUsernameMapping(String username);

    /**
     * Adds a mapping for the specified username and userId
     */
    void addUsernameMapping(String username, String userId);

    /**
     * Saves all user information to the storage file
     */
    void saveUsers();

    /**
     * Retrieves a list of all user IDs
     *
     * @return List of all user IDs
     */
    List<String> getAllUserIds();

} // End of Interface