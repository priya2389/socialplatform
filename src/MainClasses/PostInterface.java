// Package
package MainClasses;

// Imports
import java.util.List;

public interface PostInterface {

    /**
     * Retrieves unique identifier for this post
     *
     * @return the post ID
     */
    String getId();

    /**
     * Retrieves user ID of the post's author
     *
     * @return the user ID of the post's author
     */
    String getUserId();

    /**
     * Retrieves content of this post
     *
     * @return the content of the post
     */
    String getContent();

    /**
     * Retrieves number of upvotes for this post
     *
     * @return the number of upvotes
     */
    int getUpvotes();

    /**
     * Retrieves number of downvotes for this post
     *
     * @return the number of downvotes
     */
    int getDownvotes();

    /**
     * Retrieves list of comments for this post
     *
     * @return the list of comments
     */
    List<Comment> getComments();

    /**
     * Increments the upvote count for this post by 1
     */
    void upvote();

    /**
     * Checks to see if a post is an image
     */
    boolean isImage();

    /**
     * Increments the downvote count for this post by 1
     */
    void downvote();

    /**
     * Adds comment to this post
     */
    void addComment(Comment comment);

    /**
     * Deletes comment from this post by its comment ID
     */
    void deleteComment(String commentId);

    /**
     * Sanitizes and formats the post content, removing unwanted characters like newlines and tabs
     *
     * @return the sanitized content
     */
    // String sanitizeContent(String input);

    /**
     * Converts the post to a string format that's suitable for file storage
     *
     * @return a string representing the post for storage
     */
    String toFileString();

    /**
     * Creates a Post object from a string representation of post data
     *
     * @return the Post object created from the string, or null if invalid data
     */
    // static Post fromFileString(String line);

} // End of Interface