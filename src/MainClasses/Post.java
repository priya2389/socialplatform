// Package
package MainClasses;

// Imports
import Interfaces.PostInterface;
import java.util.*;


public class Post implements PostInterface {
    private String id;
    private String userId;
    private String content;
    private int upvotes;
    private int downvotes;
    private List<Comment> comments;
    private boolean imageFlag;

    public Post(String userId, String content) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        this.id = UUID.randomUUID().toString().substring(0, 13);
        this.userId = userId;
        this.content = sanitizeContent(content);
        this.upvotes = 0;
        this.downvotes = 0;
        this.comments = Collections.synchronizedList(new ArrayList<>()); // Thread-safe list
        this.imageFlag = false;
    }

    // Constructor for an image post
    public Post(String userId, String content, boolean flag) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        this.id = UUID.randomUUID().toString().substring(0, 13);
        this.userId = userId;
        this.content = content;
        this.upvotes = 0;
        this.downvotes = 0;
        this.comments = Collections.synchronizedList(new ArrayList<>());
        this.imageFlag = flag;
    }

    private Post(String id, String userId, String content, int upvotes, int downvotes, List<Comment> comments,
                 boolean flag) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.comments = Collections.synchronizedList(comments); // Thread-safe list
        this.imageFlag = flag;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public synchronized int getUpvotes() {
        return upvotes;
    }

    public synchronized int getDownvotes() {
        return downvotes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public synchronized void upvote() {
        this.upvotes++;
    }

    public synchronized void downvote() {
        this.downvotes++;
    }

    public synchronized void addComment(Comment comment) {
        comments.add(comment);
    }

    public synchronized void deleteComment(String commentId) {
        comments.removeIf(comment -> comment.getId().equals(commentId));
    }

    public synchronized boolean isImage() { return imageFlag; }

    private String sanitizeContent(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("[\\r\\n\\t]", "").trim();
    }

    public String toFileString() {
        String safeContent = content.replace("|~|", "\\|~|");
        StringBuilder postString = new StringBuilder(String.join("|~|", id, userId,
                safeContent, String.valueOf(upvotes), String.valueOf(downvotes), String.valueOf(imageFlag)));
        synchronized (comments) {
            for (Comment comment : comments) {
                postString.append("||").append(comment.toFileString());
            }
        }
        return postString.toString();
    }

    public static Post fromFileString(String line) {
        if (line == null || line.isEmpty()) {
            System.out.println("Invalid post data: line is null or empty");
            return null;
        }

        String[] parts = line.split("\\|\\|");
        if (parts.length == 0) {
            System.out.println("Invalid post data: " + line);
            return null;
        }

        String[] postDetails = parts[0].split("\\|~\\|", -1);
        if (postDetails.length < 6) {
            System.out.println("Invalid post details: " + parts[0]);
            return null;
        }

        String id = postDetails[0];
        String userId = postDetails[1];
        String content = postDetails[2].replace("\\|~|", "|~|");
        int upvotes;
        int downvotes;
        boolean flag = false;

        try {
            upvotes = Integer.parseInt(postDetails[3]);
            downvotes = Integer.parseInt(postDetails[4]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format in post data: " + parts[0]);
            return null;
        }

        try {
            flag = Boolean.parseBoolean(postDetails[5]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format in post data: " + parts[0]);
            return null;
        }

        List<Comment> comments = Collections.synchronizedList(new ArrayList<>());
        for (int i = 1; i < parts.length; i++) {
            Comment comment = Comment.fromFileString(parts[i]);
            if (comment != null) {
                comments.add(comment);
            }
        }

        return new Post(id, userId, content, upvotes, downvotes, comments, flag);
    }
}