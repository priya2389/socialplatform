// Packages
package MainClasses;

// Imports
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class UserFeed implements UserFeedInterface {
    private static final String POSTS_FILE = "DataBase/Posts.txt";
    private final FriendRequestSystem friendRequestSystem;
    private final ReentrantLock fileLock = new ReentrantLock();

    public UserFeed(FriendRequestSystem friendRequestSystem) {
        this.friendRequestSystem = friendRequestSystem;
        createPostsFile();
    }

    private void createPostsFile() {
        try {
            File file = new File(POSTS_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error creating posts file: " + e.getMessage());
        }
    }

    public void addPost(String userId, String content) {
        Post post = new Post(userId, content);
        savePostToFile(post);
    }

    public void addPostImage(String userId, String ImageString) {
        Post post = new Post(userId, ImageString, true);
        savePostToFile(post);
    }

    private void savePostToFile(Post post) {
        fileLock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(POSTS_FILE, true))) {
            writer.write(post.toFileString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving post: " + e.getMessage());
        } finally {
            fileLock.unlock();
        }
    }

    public boolean deletePost(String postId, String userId) {
        List<Post> posts = loadPosts();
        boolean postDeleted = posts.removeIf(post -> post.getId().equals(postId) && post.getUserId().equals(userId));

        if (postDeleted) {
            saveAllPosts(posts);
            return true;
        } else {
            return false;
        }
    }

    public List<Post> getAllPosts(String viewerId) {
        List<Post> posts = loadPosts();
        List<Post> visiblePosts = new ArrayList<>();

        for (Post post : posts) {
            if (isPostVisibleToUser(post, viewerId)) {
                visiblePosts.add(post);
            }
        }
        return visiblePosts;
    }

    private boolean isPostVisibleToUser(Post post, String viewerId) {
        String postAuthorId = post.getUserId();
        if (!friendRequestSystem.getUserManager().userIdExists(postAuthorId)) {
            return false;
        }
        boolean isAuthor = viewerId.equals(postAuthorId);
        boolean isFriend = friendRequestSystem.isFriend(viewerId, postAuthorId);
        boolean isEitherBlocked = friendRequestSystem.isEitherBlocked(viewerId, postAuthorId);
        return (isAuthor || isFriend) && !isEitherBlocked;
    }

    public Post getPostById(String postId) {
        List<Post> posts = loadPosts();
        for (Post post : posts) {
            if (post.getId().equals(postId)) {
                return post;
            }
        }
        return null; // Prod.Post not found
    }

    public void addComment(String postId, String userId, String content) {
        List<Post> posts = loadPosts();
        boolean found = false;

        for (Post post : posts) {
            if (post.getId().equals(postId)) {
                if (!isPostVisibleToUser(post, userId)) {
                    return;
                }
                Comment comment = new Comment(userId, postId, content);
                post.addComment(comment);
                found = true;
                break;
            }
        }

        if (found) {
            saveAllPosts(posts);
            System.out.println("Prod.Comment added successfully.");
        } else {
            System.out.println("Prod.Post not found.");
        }
    }

    public void upvotePost(String postId, String userId) {
        updatePostVotes(postId, true, userId);
    }

    public void downvotePost(String postId, String userId) {
        updatePostVotes(postId, false, userId);
    }

    private void updatePostVotes(String postId, boolean upvote, String userId) {
        List<Post> posts = loadPosts();
        boolean found = false;

        for (Post post : posts) {
            if (post.getId().equals(postId)) {
                if (!isPostVisibleToUser(post, userId)) {
                    System.out.println("You are not allowed to vote on this post.");
                    return;
                }
                if (upvote) {
                    post.upvote();
                } else {
                    post.downvote();
                }
                found = true;
                break;
            }
        }

        if (found) {
            saveAllPosts(posts);
            System.out.println("Prod.Post vote updated.");
        } else {
            System.out.println("Prod.Post not found.");
        }
    }

    public void upvoteComment(String postId, String commentId, String userId) {
        updateCommentVotes(postId, commentId, true, userId);
    }

    public void downvoteComment(String postId, String commentId, String userId) {
        updateCommentVotes(postId, commentId, false, userId);
    }

    private void updateCommentVotes(String postId, String commentId, boolean upvote, String userId) {
        List<Post> posts = loadPosts();
        boolean found = false;

        for (Post post : posts) {
            if (post.getId().equals(postId)) {
                if (!isPostVisibleToUser(post, userId)) {
                    System.out.println("You are not allowed to interact with this comment.");
                    return;
                }
                for (Comment comment : post.getComments()) {
                    if (comment.getId().equals(commentId) && isCommentVisibleToUser(comment, userId)) {
                        if (upvote) {
                            comment.upvote();
                        } else {
                            comment.downvote();
                        }
                        found = true;
                        break;
                    }
                }
                break;
            }
        }

        if (found) {
            saveAllPosts(posts);
            System.out.println("Prod.Comment vote updated.");
        } else {
            System.out.println("Prod.Comment not found.");
        }
    }

    private boolean isCommentVisibleToUser(Comment comment, String viewerId) {
        String commenterId = comment.getUserId();
        if (!friendRequestSystem.getUserManager().userIdExists(commenterId)) {
            return false;
        }
        return !friendRequestSystem.isEitherBlocked(viewerId, commenterId);
    }

    private List<Post> loadPosts() {
        List<Post> posts = new ArrayList<>();
        fileLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader(POSTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Post post = Post.fromFileString(line);
                if (post != null) {
                    posts.add(post);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading posts: " + e.getMessage());
        } finally {
            fileLock.unlock();
        }
        return posts;
    }

    private void saveAllPosts(List<Post> posts) {
        fileLock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(POSTS_FILE))) {
            for (Post post : posts) {
                writer.write(post.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving posts: " + e.getMessage());
        } finally {
            fileLock.unlock();
        }
    }

    public FriendRequestSystem getFriendRequestSystem() {
        return friendRequestSystem;
    }

    public void deleteUserPostsAndComments(String userId) {
        List<Post> posts = loadPosts();
        posts.removeIf(post -> post.getUserId().equals(userId));

        for (Post post : posts) {
            post.getComments().removeIf(comment -> comment.getUserId().equals(userId));
        }
        saveAllPosts(posts);
    }
}