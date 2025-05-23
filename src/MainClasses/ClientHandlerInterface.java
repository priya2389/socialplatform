// Packages
package MainClasses;

public interface ClientHandlerInterface {

    /**
     * Starts handling client requests and listens for commands and responds accordingly
     */
    void run();

    /**
     * Handles the request from the client by processing the command and its associated data
     * @return the response to be sent back to the client
     */
    // String handleRequest(String request);

    /**
     * Registers a new user in the system by processing the registration data
     * @return a response indicating the result of the registration
     */
    // String handleRegister(String[] parts);

    /**
     * Handles a login request by verifying the username and password
     * @return a response indicating the result of the login attempt
     */
    // String handleLogin(String[] parts);

    /**
     * Handles a search for users based on a search term and returns matching usernames
     * @return a response with the search results or a failure message
     */
    // String handleSearchUsers(String[] parts);

    /**
     * Displays the logged-in user's profile, including their name, username, and lists of friends and blocked users
     * @return a response containing the profile details or an error message if the user is not logged in
     */
    // String handleViewProfile();

    /**
     * Handles sending a friend request to another user
     * @return a response indicating whether the friend request was successfully sent
     */
    // String handleAddFriend(String[] parts);

    /**
     * Views pending friend requests for the logged-in user
     * @return a response listing the pending friend requests or indicating that there are none
     */
    // String handleViewFriendRequests();

    /**
     * Accepts a pending friend request from another user
     * @return a response indicating whether the request was successfully accepted
     */
    // String handleAcceptFriendRequest(String[] parts);

    /**
     * Declines a pending friend request from another user
     * @return a response indicating whether the request was successfully declined
     */
    // String handleDeclineFriendRequest(String[] parts);

    /**
     * Blocks a user, preventing them from interacting with the logged-in user
     * @return a response indicating whether the user was successfully blocked
     */
    // String handleBlockUser(String[] parts);

    /**
     * Unblocks a previously blocked user
     * @return a response indicating whether the user was successfully unblocked
     */
    // String handleUnblockUser(String[] parts);

    /**
     * Creates a new post for the logged-in user
     * @return a response indicating whether the post was successfully created
     */
    // String handleCreatePost(String[] parts);

    /**
     * Views the feed of posts for the logged-in user
     * @return a response containing the user's feed or indicating that there are no posts to display
     */
    // String handleViewFeed();

    /**
     * Deletes a post by its ID if it belongs to the logged-in user
     * @return a response indicating whether the post was successfully deleted
     */
    // String handleDeletePost(String[] parts);

    /**
     * Deletes the logged-in user's account, removing them from all associated data
     * @return a response indicating whether the account was successfully deleted
     */
    // String handleDeleteAccount();

    /**
     * Upvotes a post by its ID
     * @return a response indicating whether the post was successfully upvoted
     */
    // String handleUpvotePost(String[] parts);

    /**
     * Downvotes a post by its ID
     * @return a response indicating whether the post was successfully downvoted
     */
    // String handleDownvotePost(String[] parts);

    /**
     * Adds a comment to a post by its ID
     * @return a response indicating whether the comment was successfully added
     */
    // String handleAddComment(String[] parts);

    /**
     * Views the comments for a specific post
     * @return a response containing the post's comments or an error message if the post was not found
     */
    // String handleViewComments(String[] parts);

    /**
     * Converts a set of user IDs into a string of usernames
     * @return a string of usernames corresponding to the user IDs
     */
    // String getUsernamesFromIds(Set<String> userIds);

} // End of Interface