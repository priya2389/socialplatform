// Package
package MainClasses;

// Imports
import java.util.*;

public class Comment implements CommentInterface {
    private String id;
    private String userId;
    private String postId;
    private String content;
    private int upvotes;
    private int downvotes;

    public Comment(String userId, String postId, String content) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("Prod.User ID cannot be null or empty");
        }
        if (postId == null || postId.isEmpty()) {
            throw new IllegalArgumentException("Prod.Post ID cannot be null or empty");
        }
        this.id = UUID.randomUUID().toString().substring(0, 13);
        this.userId = userId;
        this.postId = postId;
        this.content = sanitizeContent(content);
        this.upvotes = 0;
        this.downvotes = 0;
    }

    private Comment(String id, String userId, String postId, String content, int upvotes, int downvotes) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getPostId() { return postId; }
    public String getContent() { return content; }
    public int getUpvotes() { return upvotes; }
    public int getDownvotes() { return downvotes; }

    public synchronized void upvote() { this.upvotes++; }
    public synchronized void downvote() { this.downvotes++; }

    private String sanitizeContent(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("[\\r\\n\\t]", "").trim();
    }

    public String toFileString() {
        String safeContent = content.replace("|~|", "\\|~|");
        return String.join("|~|", id, userId, postId, safeContent, String.valueOf(upvotes),
                String.valueOf(downvotes));
    }

    public static Comment fromFileString(String line) {
        String[] parts = line.split("\\|~\\|", -1);
        if (parts.length < 6) {
            System.out.println("Invalid comment data: " + line);
            return null;
        }
        String id = parts[0];
        String userId = parts[1];
        String postId = parts[2];
        String content = parts[3].replace("\\|~|", "|~|");

        int upvotes;
        int downvotes;
        try {
            upvotes = Integer.parseInt(parts[4]);
            downvotes = Integer.parseInt(parts[5]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format in comment data: " + line);
            return null;
        }

        return new Comment(id, userId, postId, content, upvotes, downvotes);
    }
}