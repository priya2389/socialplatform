// Package
package MainClasses; 

// Imports
import Interfaces.ClientHandlerInterface;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;



public class ClientHandler implements Runnable, ClientHandlerInterface {
    private Socket clientSocket;
    private UserManager userManager;
    private FriendRequestSystem friendRequestSystem;
    private UserFeed userFeed;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String loggedInUserId;

    public ClientHandler(Socket clientSocket, UserManager userManager, FriendRequestSystem friendRequestSystem,
                         UserFeed userFeed) {
        this.clientSocket = clientSocket;
        this.userManager = userManager;
        this.friendRequestSystem = friendRequestSystem;
        this.userFeed = userFeed;
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error initializing client handler streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            boolean running = true;
            while (running) {
                String request = (String) in.readObject();
                if (request.equals("QUIT")) {
                    running = false;
                    continue;
                }
                String response = handleRequest(request);
                out.writeObject(response);
                out.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected.");
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing client handler resources: " + e.getMessage());
            }
        }
    }

    private String handleRequest(String request) {
        String[] parts = request.split("\\|");
        String command = parts[0];

        switch (command) {
            case "REGISTER":
                return handleRegister(parts);
            case "LOGIN":
                return handleLogin(parts);
            case "SEARCH_USERS":
                return handleSearchUsers(parts); // Ensure search functionality is active
            case "VIEW_PROFILE":
                return handleViewProfile();
            case "ADD_FRIEND":
                return handleAddFriend(parts);
            case "VIEW_FRIEND_REQUESTS":
                return handleViewFriendRequests();
            case "ACCEPT_FRIEND_REQUEST":
                return handleAcceptFriendRequest(parts);
            case "DECLINE_FRIEND_REQUEST":
                return handleDeclineFriendRequest(parts);
            case "BLOCK_USER":
                return handleBlockUser(parts);
            case "UNBLOCK_USER":
                return handleUnblockUser(parts);
            case "CREATE_POST":
                return handleCreatePost(parts);
            case "VIEW_FEED":
                return handleViewFeed();
            case "DELETE_POST":
                return handleDeletePost(parts);
            case "DELETE_ACCOUNT":
                return handleDeleteAccount();
            case "UPVOTE_POST":
                return handleUpvotePost(parts);
            case "DOWNVOTE_POST":
                return handleDownvotePost(parts);
            case "ADD_COMMENT":
                return handleAddComment(parts);
            case "VIEW_COMMENTS":
                return handleViewComments(parts);
            case "UPVOTE_COMMENT":
                return handleUpvoteComment(parts);
            case "DOWNVOTE_COMMENT":
                return handleDownVoteComment(parts);
            case "CHANGE_USERNAME":
                return handleChangeUsername(parts);
            case "CHANGE_PASSWORD":
                return handleChangePassword(parts);
            case "UPLOAD_PROFILE_PICTURE":
                return handleUploadPFP(parts);
            case "VIEW_SELF_POSTS":
                return handleViewSelfPosts();
            case "VIEW_PFP":
                return handleViewPFP();
            case "CREATE_POST_IMAGE":
                return handleCreatePostImage(parts);
            default:
                return "FAILURE|Unknown command.";
        }
    }

    private String handleSearchUsers(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid search users command.";
        }
        String searchTerm = parts[1].trim(); // Trim whitespace to avoid empty searches
        if (searchTerm.isEmpty()) {
            return "FAILURE|Search term cannot be empty.";
        }

        List<String> matchedUsers = friendRequestSystem.searchUsersByUsername(searchTerm);

        if (matchedUsers.isEmpty())
            return "SUCCESS|No users found matching the search term.";

        List<String> finalUsers = new ArrayList<>();
        for (String users : matchedUsers) {
            if (friendRequestSystem.isBlocked(userManager.getUserIdByUsername(users), loggedInUserId)
                    && !matchedUsers.isEmpty()) {
                continue;
            } else {
                finalUsers.add(users);
            }
        }

        if (finalUsers.isEmpty())
            return "SUCCESS|No users found matching the search term.";

        int counter = 1;
        StringBuilder result = new StringBuilder("SUCCESS|");
        for (String username : matchedUsers) {
            result.append(String.format("%d: ", counter++)).append(username).append("\n");
        }
        return result.toString();
    }

    private String handleRegister(String[] parts) {
        if (parts.length < 4) {
            return "FAILURE|Invalid register command.";
        }
        String name = parts[1];
        String username = parts[2];
        String password = parts[3];
        String returnMe;
        String result = userManager.registerUser(name, username, password);
        if (result.equalsIgnoreCase("usernameError")) {
            returnMe = "FAILURE|Username already exists.";}
        else if (result.equalsIgnoreCase("passwordError")) {
            returnMe = "FAILURE|Password must be at least 8 characters long, contain one uppercase letter, " +
                    "one digit, and one special character";}
        else {
            returnMe = "SUCCESS|Account created successfully.";}
        System.out.println(returnMe);
        return returnMe;
    }

    private String handleLogin(String[] parts) {
        if (parts.length < 3) {
            return "FAILURE|Invalid login command.";
        }
        String username = parts[1];
        String password = parts[2];
        boolean loggedIn = userManager.loginUser(username, password);
        if (loggedIn) {
            loggedInUserId = userManager.getUserIdByUsername(username);
            return "SUCCESS|" + loggedInUserId;
        } else {
            return "FAILURE|Invalid username or password.";
        }
    }

    private String handleViewProfile() {
        if (loggedInUserId == null) {
            return "FAILURE|User not logged in.";
        }
        User user = new User(loggedInUserId);
        StringBuilder profile = new StringBuilder();
        profile.append(user.getName()).append("\n");
        profile.append(user.getUsername()).append("\n");
        profile.append(getUsernamesFromIds(user.getFriendList())).append("\n");
        profile.append(getUsernamesFromIds(user.getBlockedList())).append("\n");
        return "SUCCESS|" + profile.toString();
    }

    private String handleAddFriend(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid add friend command.";
        }
        String friendUsername = parts[1];
        String friendId = userManager.getUserIdByUsername(friendUsername);

        if (friendId == null) {
            return "FAILURE|User not found.";
        } else if (friendId.equals(loggedInUserId)) {
            return "FAILURE|You cannot add yourself as a friend.";
        } else {
            friendRequestSystem.sendFriendRequest(loggedInUserId, friendId);
            return "SUCCESS|Friend request sent.";
        }
    }

    private String handleViewFriendRequests() {
        if (loggedInUserId == null) {
            return "FAILURE|User not logged in.";
        }
        List<String> requests = friendRequestSystem.getPendingRequests(loggedInUserId);
        if (requests.isEmpty()) {
            return "SUCCESS|No pending friend requests.";
        } else {
            StringBuilder response = new StringBuilder("SUCCESS|");
            for (String requesterId : requests) {
                String requesterUsername = userManager.getUsernameById(requesterId);
                response.append(requesterUsername).append("\n");
            }
            return response.toString();
        }
    }

    private String handleAcceptFriendRequest(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid accept friend request command.";
        }
        String requesterUsername = parts[1];
        String requesterId = userManager.getUserIdByUsername(requesterUsername);
        if (requesterId == null) {
            return "FAILURE|User not found.";
        }
        friendRequestSystem.acceptFriendRequest(loggedInUserId, requesterId);
        return "SUCCESS|Friend request accepted.";
    }

    private String handleDeclineFriendRequest(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid decline friend request command.";
        }
        String requesterUsername = parts[1];
        String requesterId = userManager.getUserIdByUsername(requesterUsername);
        if (requesterId == null) {
            return "FAILURE|User not found.";
        }
        friendRequestSystem.declineFriendRequest(loggedInUserId, requesterId);
        return "SUCCESS|Friend request declined.";
    }

    private String handleBlockUser(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid block user command.";
        }
        String blockUsername = parts[1];
        String blockId = userManager.getUserIdByUsername(blockUsername);

        if (blockId == null) {
            return "FAILURE|User not found.";
        } else if (blockId.equals(loggedInUserId)) {
            return "FAILURE|You cannot block yourself.";
        } else {
            friendRequestSystem.blockUser(loggedInUserId, blockId);
            return "SUCCESS|User blocked successfully.";
        }
    }

    private String handleUnblockUser(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid unblock user command.";
        }
        String unblockUsername = parts[1];
        String unblockId = userManager.getUserIdByUsername(unblockUsername);

        if (unblockId == null) {
            return "FAILURE|User not found.";
        } else {
            friendRequestSystem.unblockUser(loggedInUserId, unblockId);
            return "SUCCESS|User unblocked successfully.";
        }
    }

    private String handleCreatePost(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid create post command.";
        }
        String content = parts[1];
        userFeed.addPost(loggedInUserId, content);
        return "SUCCESS|Post created successfully.";
    }

    private String handleViewFeed() {
        if (loggedInUserId == null) {
            return "FAILURE|User not logged in.";
        }
        List<Post> posts = userFeed.getAllPosts(loggedInUserId);
        if (posts.isEmpty()) {
            return "FAILURE|No posts to display.";
        }
        StringBuilder feed = new StringBuilder("SUCCESS|");
        for (Post post : posts) {
            String authorUsername = userManager.getUsernameById(post.getUserId());
            feed.append("Post ID: ").append(post.getId()).append("\n");
            feed.append("Author: ").append(authorUsername).append("\n");
            if (post.isImage()) {
                feed.append("Content: ").append("IMAGE;").append(post.getContent()).append("\n");
            } else {
                feed.append("Content: ").append(post.getContent()).append("\n");
            }
            feed.append(post.getUpvotes()).append("&").append(post.getDownvotes()).append("\n");
            feed.append("---\n");
        }
        return feed.toString();
    }

    private String handleDeletePost(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid delete post command.";
        }
        String postId = parts[1];
        boolean response = userFeed.deletePost(postId, loggedInUserId);

        if (response) {
            return "SUCCESS|Post deleted successfully.";
        } else {
            return "FAILURE|Unable to delete post.";
        }
    }

    private String handleDeleteAccount() {
        if (loggedInUserId == null) {
            return "FAILURE|User not logged in.";
        }
        friendRequestSystem.removeUserFromPendingRequests(loggedInUserId);
        List<String> allUserIds = userManager.getAllUserIds();
        for (String userId : allUserIds) {
            if (!userId.equals(loggedInUserId)) {
                User user = new User(userId);
                if (user.getFriendList().remove(loggedInUserId) | user.getBlockedList().remove(loggedInUserId)) {
                    user.saveUserToFile();
                }
            }
        }
        userFeed.deleteUserPostsAndComments(loggedInUserId);
        String username = userManager.getUsernameById(loggedInUserId);
        userManager.deleteUser(username);
        loggedInUserId = null;
        return "SUCCESS|Your account has been deleted.";
    }

    private String handleUpvotePost(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid upvote post command.";
        }
        String postId = parts[1];
        userFeed.upvotePost(postId, loggedInUserId);
        return "SUCCESS|Post upvoted successfully.";
    }

    private String handleDownvotePost(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid downvote post command.";
        }
        String postId = parts[1];
        userFeed.downvotePost(postId, loggedInUserId);
        return "SUCCESS|Post downvoted successfully.";
    }

    private String handleAddComment(String[] parts) {
        if (parts.length < 3) {
            return "FAILURE|Invalid add comment command.";
        }
        String postId = parts[1];
        String content = parts[2];
        userFeed.addComment(postId, loggedInUserId, content);
        return "SUCCESS|Comment added successfully.";
    }

    private String handleUpvoteComment(String[] parts) {
        if (parts.length < 3) {
            return "FAILURE|Invalid update comment command.";
        }
        String postId = parts[1];
        Post post = userFeed.getPostById(postId);
        if (post == null) {
            return "FAILURE|Post not found.";
        }

        String commentID = parts[2];
        for (Comment comment : post.getComments()) {
            if (commentID.equals(comment.getId())) {
                userFeed.upvoteComment(postId, commentID, loggedInUserId);
                return "SUCCESS|Comment upvoted successfully.";
            }
        }

        return "FAILURE|Comment not found.";
    }

    private String handleDownVoteComment(String[] parts) {
        if (parts.length < 3) {
            return "FAILURE|Invalid update comment command.";
        }
        String postId = parts[1];
        Post post = userFeed.getPostById(postId);
        if (post == null) {
            return "FAILURE|Post not found.";
        }

        String commentID = parts[2];
        for (Comment comment : post.getComments()) {
            if (commentID.equals(comment.getId())) {
                userFeed.downvoteComment(postId, commentID, loggedInUserId);
                return "SUCCESS|Comment downvoted successfully.";
            }
        }

        return "FAILURE|Comment not found.";
    }

    private String handleViewComments(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid view comments command.";
        }
        String postId = parts[1];
        Post post = userFeed.getPostById(postId);
        if (post == null) {
            return "FAILURE|Post not found.";
        }
        StringBuilder comments = new StringBuilder();

        List<Comment> currentComments = post.getComments();

        if (currentComments == null || currentComments.isEmpty()) {
            return "FAILURE|No comments found.";
        }

        for (Comment comment : currentComments) {
            String commenterUsername = userManager.getUsernameById(comment.getUserId());
            comments.append("Author: ").append(commenterUsername).append("\n");
            comments.append("Content: ").append(comment.getContent()).append("\n");
            comments.append(comment.getUpvotes()).append("&").append(comment.getDownvotes()).append("\n");
            comments.append("Comment ID: ").append(comment.getId()).append("\n");
            comments.append("---\n");
        }
        return comments.toString();
    }

    private String handleChangeUsername(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid change username command.";
        }

        String newUserName = parts[1];

        if (userManager.userExists(newUserName)) {
            return "FAILURE|Username already exists.";
        } else {
            User user = new User(loggedInUserId);
            boolean success = user.changeUsername(newUserName, userManager);

            if (!success) {
                return "FAILURE|Username already exists.";
            } else {
                return "SUCCESS|Changed username successfully.";
            }
        }

    }

    private String handleChangePassword(String[] parts) {
        if (parts.length < 3) {
            return "FAILURE|Invalid change password command.";
        }

        String currentPassword = parts[1];
        String newPassword = parts[2];
        User user = new User(loggedInUserId);

        if (!user.getPassword().equals(currentPassword)) {
            return "FAILURE|Current password does not match.";
        } else if (!userManager.isValidPassword(newPassword)) {
            return "FAILURE|Password does not meet security criteria.";
        } else {
            user.changePassword(newPassword);
            return "SUCCESS|Changed password successfully.";
        }
    }

    private String handleUploadPFP(String[] parts) {
        if (parts.length < 2) {
            return "FAILURE|Invalid upload PFP command.";
        }
        String imageString = parts[1];
        User user = new User(loggedInUserId);

        if (imageString.equals("NO_PFP") && user.getProfilePictureString().equals("NO_PFP")) {
            return "FAILURE|There is no current PFP profile picture.";
        } else if (user.getProfilePictureString().equals("NO_PFP")) {
            user.changeProfilePicture(imageString);
            return "SUCCESS|Upload PFP successfully.";
        } else {
            user.changeProfilePicture(imageString);
            return "SUCCESS|Modified PFP successfully.";
        }
    }

    private String handleViewSelfPosts() {
        String posts = userManager.getUserPosts(loggedInUserId);

        if (posts.equals("ERROR") || posts.equals("NO POSTS FOUND")) {
            return "FAILURE|No posts found.";
        }

        return "SUCCESS|" + posts;
    }

    private String handleViewPFP() {
        User user = new User(loggedInUserId);
        String pfp = user.getProfilePictureString();

        if (pfp.equals("NO_PFP")) {
            return "FAILURE|No profile picture found.";
        } else {
            return "SUCCESS|" + pfp;
        }

    }

    private String handleCreatePostImage(String parts[]) {
        if (parts.length < 2) {
            return "FAILURE|Invalid create post command.";
        }
        String content = parts[1];
        userFeed.addPostImage(loggedInUserId, content);
        return "SUCCESS|Post created successfully.";
    }

    private String getUsernamesFromIds(Set<String> userIds) {
        StringBuilder usernames = new StringBuilder();
        for (String userId : userIds) {
            String username = userManager.getUsernameById(userId);
            if (username != null) {
                usernames.append(username).append(", ");
            }
        }
        if (usernames.length() > 0) {
            usernames.setLength(usernames.length() - 2); // Remove trailing comma and space
        }
        return usernames.toString();
    }
}