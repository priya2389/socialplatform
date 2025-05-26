// Package
package Interfaces;

// Imports
import java.util.List;

import MainClasses.UserManager;

public interface FriendInterface {

    /**
     * Loads pending friend requests from a file
     */
    // void loadPendingRequests();

    /**
     * Saves current pending friend requests to a file
     */
    // void savePendingRequests();

    /**
     * Sends friend request from one user to another
     */
    void sendFriendRequest(String userId, String friendId);

    /**
     * Accepts friend request from a user
     */
    void acceptFriendRequest(String userId, String requesterId);

    /**
     * Declines pending friend request from a user
     */
    void declineFriendRequest(String userId, String requesterId);

    /**
     * Removes a user from the friend list of another user
     */
    void removeFriend(String userId, String friendId);

    /**
     * Blocks a user which removes them from the friend list and adds them to the blocked list
     */
    void blockUser(String userId, String blockId);

    /**
     * Unblocks a user which removes them from the blocked list
     */
    void unblockUser(String userId, String unblockId);

    /**
     * Checks if two users are friends
     *
     * @return true if the users are friends, false if they aren't
     */
    boolean isFriend(String userId, String otherUserId);

    /**
     * Checks if a user has blocked another user
     *
     * @return true if the user has blocked the other user, false if the user hasn't
     */
    boolean isBlocked(String userId, String otherUserId);

    /**
     * Checks if either of two users has blocked the other
     *
     * @return true if either user has blocked the other, false if not
     */
    boolean isEitherBlocked(String userId, String otherUserId);

    /**
     * Retrieves list of pending friend requests for a user
     *
     * @return a list of user IDs who have sent friend requests
     */
    List<String> getPendingRequests(String userId);

    /**
     * Removes a user from the list of pending friend requests
     */
    void removeUserFromPendingRequests(String userId);

    /**
     * Searches for users by a search term in their usernames
     *
     * @return a list of usernames that match the search term
     */
    List<String> searchUsersByUsername(String searchTerm);

    /**
     * Displays the list of all users in the system
     */
    void displayUserList();

    /**
     * Retrieves user manager instance associated with this system
     *
     * @return the UserManager instance
     */
    UserManager getUserManager();

} // End of Interface