// Package
package MainClasses;

// Imports

import java.util.List;

public interface UserFeedInterface {

    /**
     * Adds a new post to the feed and saves it to the posts file
     */
    void addPost(String userId, String content);

    /**
     * Adds a new IMAGE post to the feed, then saves it to the posts file
     */
    void addPostImage(String userId, String imageString);

    /**
     * Deletes a post by its ID and the user ID of the author
     */
    boolean deletePost(String postId, String userId);

    /**
     * Retrieves all posts visible to the specified user
     *
     * @return a list of posts visible to the user
     */
    List<Post> getAllPosts(String viewerId);

    /**
     * Adds a comment to a post
     */
    void addComment(String postId, String userId, String content);

    /**
     * Upvotes a post
     */
    void upvotePost(String postId, String userId);

    /**
     * Downvotes a post
     */
    void downvotePost(String postId, String userId);

    /**
     * Upvotes a comment on a post
     */
    void upvoteComment(String postId, String commentId, String userId);

    /**
     * Downvotes a comment on a post
     */
    void downvoteComment(String postId, String commentId, String userId);

    /**
     * Deletes all posts and comments created by a user
     */
    void deleteUserPostsAndComments(String userId);

    /**
     * Retrieves a specific post by its ID
     *
     * @return the post with the given ID, or null if not found
     */
    Post getPostById(String postId);

    /**
     * Retrieves the FriendRequestSystem associated with this feed
     *
     * @return the FriendRequestSystem instance
     */
    FriendRequestSystem getFriendRequestSystem();

} // End of Interface