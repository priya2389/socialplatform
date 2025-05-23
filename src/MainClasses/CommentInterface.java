// Package
package MainClasses;

public interface CommentInterface {

    /**
     * Retrieves unique identifier for this comment
     *
     * @return the comment ID
     */
    String getId();

    /**
     * Retrieves user ID of the comment's author
     *
     * @return the user ID of the comment's author
     */
    String getUserId();

    /**
     * Retrieves post ID to which this comment belongs
     *
     * @return the post ID
     */
    String getPostId();

    /**
     * Retrieves content of this comment
     *
     * @return the content of the comment
     */
    String getContent();

    /**
     * Retrieves number of upvotes for this comment
     *
     * @return the number of upvotes
     */
    int getUpvotes();

    /**
     * Retrieves number of downvotes for this comment
     *
     * @return the number of downvotes
     */
    int getDownvotes();

    /**
     * Increments the upvote count for this comment by 1
     */
    void upvote();

    /**
     * Increments the downvote count for this comment by 1
     */
    void downvote();

    /**
     * Sanitizes the comment content by removing unwanted characters like newlines and tabs
     * @return the sanitized content
     */
    // String sanitizeContent(String input);

    /**
     * Converts the comment to a string format appropriate for file storage
     *
     * @return a string representing the comment for storage
     */
    String toFileString();

    /**
     * Creates a Comment object from a string representation of comment data
     * @return the Comment object created from the string, or null if invalid data
     */
    // static Comment fromFileString(String line);

} // End of Interface