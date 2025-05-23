// Package
package MainClasses;

// Imports
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import javax.imageio.*;
import java.util.Base64;
import java.util.Set;

public class ClientMainMenu implements ClientMainMenuInterface {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isLoggedIn = false;
    private boolean connectionTimeout = false;
    private String loggedInUsername;
    private Set<String> hiddenPostIds = new HashSet<>();
    private Set<String> hiddenUsernames = new HashSet<>();

    public ClientMainMenu(String host, int port) {
        int waitTimeMilliseconds = 5000;
        int timeOutMilliseconds = 60000; // Default is 60 seconds (1 min)

        while (true) {
            if (waitTimeMilliseconds > timeOutMilliseconds) { // Defines the timeout period
                System.out.println("\nServer is unreachable at this time! Try again later.");
                connectionTimeout = true;
                return;
            }
            try {
                socket = new Socket(host, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                System.out.println("Connected to the server.");
                break;
            } catch (IOException e) {
                System.out.printf("Unable to connect to server! Reconnecting in %s seconds...\n",
                        waitTimeMilliseconds / 1000);

                try {
                    Thread.sleep(waitTimeMilliseconds);
                    waitTimeMilliseconds += 5000;
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    System.out.println("Retry was interrupted!");
                }

            }
        }
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (!isLoggedIn) {
                System.out.println("\n=== Welcome to the Social Media App ===");
                System.out.println("1. Create Account");
                System.out.println("2. Log In");
                System.out.println("3. Quit");
                System.out.print("Choose an option: ");
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        createAccount(scanner);
                        break;
                    case "2":
                        login(scanner);
                        break;
                    case "3":
                        quit();
                        return;
                    default:
                        System.out.println("\nInvalid choice. Please select 1, 2, or 3.");
                }
            } else {
                userMenu(scanner);
            }
        }
    }

    private void createAccount(Scanner scanner) {
        try {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter a username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter a password: ");
            String password = scanner.nextLine();

            sendRequest("REGISTER|" + name + "|" + username + "|" + password);
            String response = (String) in.readObject();
            System.out.println(response.contains("SUCCESS") ? "\nAccount created successfully!" :
                    "\n" + response.split("\\|")[1]);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nError creating account: " + e.getMessage());
        }
    }

    private void login(Scanner scanner) {
        try {
            System.out.print("Enter your username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            sendRequest("LOGIN|" + username + "|" + password);
            String response = (String) in.readObject();
            if (response.startsWith("SUCCESS")) {
                isLoggedIn = true;
                loggedInUsername = username;
            } else {
                System.out.println("\n" + response.split("\\|")[1]);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error logging in: " + e.getMessage());
        }
    }

    private void userMenu(Scanner scanner) {
        while (isLoggedIn) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. View Profile");
            System.out.println("2. Add Friend");
            System.out.println("3. View Friend Requests");
            System.out.println("4. Block User");
            System.out.println("5. Unblock User");
            System.out.println("6. Create Post");
            System.out.println("7. View Feed");
            System.out.println("8. Delete Account");
            System.out.println("9. Delete Post (ID Required)");
            System.out.println("10. Search Users");
            System.out.println("11. Hide User Posts");
            System.out.println("12. Log Out");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewProfile(scanner);
                    break;
                case "2":
                    addFriend(scanner);
                    break;
                case "3":
                    viewFriendRequests(scanner);
                    break;
                case "4":
                    blockUser(scanner);
                    break;
                case "5":
                    unblockUser(scanner);
                    break;
                case "6":
                    createPost(scanner);
                    break;
                case "7":
                    viewFeed(scanner);
                    break;
                case "8":
                    deleteAccount(scanner);
                    break;
                case "9":
                    deletePost(scanner);
                    break;
                case "10":
                    searchUsers(scanner);
                    break;
                case "11":
                    hidePost(scanner);
                    break;
                case "12":
                    logout();
                    return;
                default:
                    System.out.println("\nInvalid choice. Please select a number from 1 to 12.");
            }
        }
    }

    private void hidePost(Scanner scanner) {
        while (true) {
            System.out.println("\n=== Hide User Posts ===");
            System.out.println("1. Hide a specific post by Post ID");
            System.out.println("2. Hide all posts from a user by username");
            System.out.println("3. Return to User Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter the Post ID to hide: ");
                    String postId = scanner.nextLine().trim();
                    if (postId.isEmpty()) {
                        System.out.println("Post ID cannot be empty. Please try again.");
                        break;
                    }
                    hiddenPostIds.add(postId);
                    System.out.println("\nPost has been hidden.");
                    break;
                case "2":
                    System.out.print("Enter the username to hide all their posts: ");
                    String username = scanner.nextLine().trim();
                    if (username.equalsIgnoreCase(loggedInUsername)) {
                        System.out.println("\nYou cannot hide your own posts.");
                        break;
                    }
                    if (username.isEmpty()) {
                        System.out.println("\nUsername cannot be empty. Please try again.");
                        break;
                    }
                    hiddenUsernames.add(username);
                    System.out.println("\nAll posts from the user have been hidden.");
                    break;
                case "3":
                    return; // Return to the main menu
                default:
                    System.out.println("\nInvalid choice. Please select a number from 1 to 3.");
            }
        }
    }

    private void viewProfile(Scanner scanner) {
        System.out.println();
        sendRequest("VIEW_PROFILE");
        try {
            String response = (String) in.readObject();

            if (response.startsWith("FAILURE")) {
                System.out.println("\n" + response.split("\\|")[1]);
                return;
            }

            while (true) {
                System.out.println(response.contains("SUCCESS") ? response.substring(8) : response);

                System.out.println("Profile Options: ");
                System.out.println("1. Change Username");
                System.out.println("2. Change Password");
                System.out.println("3. Upload a Profile Picture");
                System.out.println("4. View your Profile Picture");
                System.out.println("5. Return to User Menu");
                System.out.print("Choose an option: ");

                String choice = scanner.nextLine().trim();

                String output = "";
                switch (choice) {
                    case "1":
                        System.out.print("Enter your new username: ");
                        String newUsername = scanner.nextLine().trim();
                        sendRequest("CHANGE_USERNAME|" + newUsername);

                        output = (String) in.readObject();
                        System.out.println(output.split("\\|")[1]);

                        return;
                    case "2":
                        System.out.print("Enter your current password: ");
                        String currentPassword = scanner.nextLine().trim();

                        System.out.print("Enter your new password: ");
                        String newPassword = scanner.nextLine().trim();

                        sendRequest("CHANGE_PASSWORD|" + currentPassword + "|" + newPassword);

                        output = (String) in.readObject();
                        System.out.println(output.split("\\|")[1]);
                        return;
                    case "3":
                        System.out.println("\nPlease upload an image!");
                        uploadProfilePicture();
                        return;
                    case "4":
                        viewPFP();
                        return;
                    case "5":
                        return;
                    default:
                        System.out.println("\nNot a valid input");
                        break;
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nError viewing profile: " + e.getMessage());
        }
    }

    private void addFriend(Scanner scanner) {
        System.out.println();
        System.out.print("Enter the username of the person you want to add as a friend: ");
        String friendUsername = scanner.nextLine().trim();
        sendRequest("ADD_FRIEND|" + friendUsername);
        try {
            String response = (String) in.readObject();
            System.out.println(response.split("\\|")[1]);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nError adding friend: " + e.getMessage());
        }
    }

    private void viewFriendRequests(Scanner scanner) {
        System.out.println();
        sendRequest("VIEW_FRIEND_REQUESTS");
        try {
            String response = (String) in.readObject();
            String[] parts = response.split("\\|");

            if (parts[0].contains("SUCCESS") && parts[1].contains("No pending friend requests.")) {
                System.out.println(parts[1]);
            } else if (parts[0].contains("SUCCESS")) {
                String[] requests = parts[1].split("\n");

                int index = 0;
                while (true) {
                    if (index >= requests.length) {
                        System.out.println("End of friend requests.");
                        return;
                    }

                    System.out.println("Friend request from: " + requests[index]);
                    System.out.println("\n=== Options ===");
                    System.out.println("1. Accept friend request");
                    System.out.println("2. Decline friend request");
                    System.out.println("3. Go to next request");
                    System.out.println("4. Return to User Menu");
                    System.out.print("Choose an option: ");

                    String choice = scanner.nextLine().trim();

                    switch (choice) {
                        case "1":
                            sendRequest("ACCEPT_FRIEND_REQUEST|" + requests[index]);
                            try {
                                response = (String) in.readObject();
                                System.out.println(response.split("\\|")[1]);
                            } catch (IOException | ClassNotFoundException e) {
                                System.out.println("Error accepting friend request: " + e.getMessage());
                            }
                            index++;
                            break;
                        case "2":
                            sendRequest("DECLINE_FRIEND_REQUEST|" + requests[index]);
                            try {
                                response = (String) in.readObject();
                                System.out.println(response.split("\\|")[1]);
                            } catch (IOException | ClassNotFoundException e) {
                                System.out.println("Error declining friend request: " + e.getMessage());
                            }
                            index++;
                            break;
                        case "3":
                            index++;
                            break;
                        case "4":
                            return;
                        default:
                            System.out.println("Invalid choice. Please select a number from 1 to 4.");
                            break;
                    }


                }


            } else {
                System.out.println(parts[1]);
            }


        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error viewing friend requests: " + e.getMessage());
        }
    }

    private void blockUser(Scanner scanner) {
        System.out.println();
        System.out.print("Enter the username of the person you want to block: ");
        String blockUsername = scanner.nextLine().trim();
        sendRequest("BLOCK_USER|" + blockUsername);
        try {
            String response = (String) in.readObject();
            System.out.println(response.split("\\|")[1]);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nError blocking user: " + e.getMessage());
        }
    }

    private void unblockUser(Scanner scanner) {
        System.out.println();
        System.out.print("Enter the username of the person you want to unblock: ");
        String unblockUsername = scanner.nextLine().trim();
        sendRequest("UNBLOCK_USER|" + unblockUsername);
        try {
            String response = (String) in.readObject();
            System.out.println(response.split("\\|")[1]);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nError unblocking user: " + e.getMessage());
        }
    }

    private void createPost(Scanner scanner) {
        String content = "";
        String request = "";

        while (true) {
            boolean exit = false;
            System.out.println();
            System.out.println("=== What type of post would you like to create? === ");
            System.out.println("1. Image Post");
            System.out.println("2. Text Post");
            System.out.println("3. Return to User Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("Please choose a file to upload");

                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files",
                            "jpg", "png", "jpeg");

                    chooser.setFileFilter(filter);
                    chooser.setDialogTitle("Select a post image to upload");
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int userSelection = chooser.showOpenDialog(null);
                    String[] validExtensions = {".jpg", ".jpeg", ".png"};

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        boolean valid = false;

                        String fileNameExtension = file.getName().substring(file.getName().indexOf("."));

                        for (String extension : validExtensions) {
                            if (fileNameExtension.equalsIgnoreCase(extension)) {
                                valid = true;
                                break;
                            }
                        }

                        if (!valid) {
                            System.out.println("\nInvalid file type! (Must end in .jpg, .jpeg, or .png)");
                            return;
                        }


                        try (FileInputStream fis = new FileInputStream(file)) {
                            byte[] imageToByte = new byte[(int) file.length()];
                            fis.read(imageToByte);

                            content = Base64.getEncoder().encodeToString(imageToByte);
                            request = "CREATE_POST_IMAGE|";
                            exit = true;
                        } catch (IOException e) {
                            System.out.println("\nError encoding image file: " + e.getMessage());
                            return;
                        }
                    } else {
                        System.out.println("\nNo file selected.");
                        return;
                    }
                    break;
                case "2":
                    System.out.print("Enter the content of your post: ");
                    content = scanner.nextLine().trim();
                    request = "CREATE_POST|";
                    exit = true;
                    break;
                case "3":
                    return;
                default:
                    System.out.println("\nInvalid choice. Please select a number from 1 to 3.");
                    break;
            }
            if (exit) {
                break;
            }
        }

        sendRequest(request + content);
        try {
            String response = (String) in.readObject();
            System.out.println(response.split("\\|")[1]);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nError creating post: " + e.getMessage());
        }
    }

    private void viewFeed(Scanner scanner) {
        sendRequest("VIEW_FEED");
        try {
            String response = (String) in.readObject();
            if (response.startsWith("SUCCESS")) {
                displayPosts(response.substring(8), scanner);
            } else {
                System.out.println("\n" + response.split("\\|")[1]);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nError viewing feed: " + e.getMessage());
        }
    }

    private void displayPosts(String feedContent, Scanner scanner) {
        String feed = feedContent;
        String[] posts = feed.split("---\n");
        int index = 0;
        boolean viewed = false;

        while (true) {
            if (index >= posts.length) {
                System.out.println("\nEnd of posts.");
                return;
            }
            String postContent = posts[index];
            String currentPostId = null;
            String postAuthor = null;
            String currentImmageString = null;


            // Extract Post ID and Author
            if (postContent.contains("Post ID:")) {
                currentPostId = postContent.split("Post ID: ")[1].split("\n")[0];
            }
            if (postContent.contains("Author:")) {
                postAuthor = postContent.split("Author: ")[1].split("\n")[0];
            }

            // Check if post is hidden
            if (hiddenPostIds.contains(currentPostId) || hiddenUsernames.contains(postAuthor)) {
                index++;
                continue; // Skip this post and move to the next one
            }


            // If post is an image, extract that info and display image
            if (postContent.contains("IMAGE")) {
                currentImmageString = postContent.split("Content: ")[1].split("\n")[0]
                        .split(";")[1];

                postContent = postContent.replace(";" + currentImmageString, "");

                if (!viewed) {
                    try {
                        byte[] imageToByte = Base64.getDecoder().decode(currentImmageString);
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageToByte));

                        JFrame imageFrame = new JFrame();
                        imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        ImageIcon imageIcon = new ImageIcon(image);

                        JLabel label = new JLabel(imageIcon);

                        label.setHorizontalAlignment(JLabel.CENTER);
                        label.setVerticalAlignment(JLabel.CENTER);

                        imageFrame.setLayout(new BorderLayout());
                        imageFrame.add(label, BorderLayout.CENTER);

                        imageFrame.pack();
                        imageFrame.setVisible(true);
                    } catch (IOException | NullPointerException e) {
                        System.out.println("\nError reading image post file! Moving to next post!");
                        index++;
                        continue;
                    }
                }

            }

            boolean refresh = false;

            System.out.println();
            System.out.println(postContent);
            System.out.println("=== Post Options ===");
            System.out.println("1. Upvote Post");
            System.out.println("2. Downvote Post");
            System.out.println("3. Add Comment");
            System.out.println("4. View Comments");
            System.out.println("5. View Next Post");
            System.out.println("6. Return to User Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    sendRequest("UPVOTE_POST|" + currentPostId);
                    try {
                        String response = (String) in.readObject();
                        System.out.println(response.split("\\|")[1]);
                        refresh = true;
                        viewed = true;

                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("\nError in upvoting post!");
                    }
                    break;
                case "2":
                    sendRequest("DOWNVOTE_POST|" + currentPostId);
                    try {
                        String response = (String) in.readObject();
                        System.out.println(response.split("\\|")[1]);
                        refresh = true;
                        viewed = true;

                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("\nError in downvoting post!");
                    }
                    break;
                case "3":
                    System.out.print("Enter Comment: ");
                    sendRequest("ADD_COMMENT|" + currentPostId + "|" + scanner.nextLine().trim());

                    try {
                        String response = (String) in.readObject();
                        System.out.println(response.split("\\|")[1]);
                        viewed = true;

                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("\nError in adding comment!");
                    }
                    break;
                case "4":
                    viewComments(scanner, currentPostId);
                    viewed = false;
                    break;
                case "5":
                    index++;
                    break;
                case "6":
                    return;
                default:
                    System.out.println("\nInvalid choice. Please select an option from 1 to 6.");
                    break;
            }

            // Refresh Logic
            if (refresh) {
                sendRequest("VIEW_FEED");
                try {
                    String response = (String) in.readObject();
                    if (response.startsWith("SUCCESS")) {
                        feed = response.substring(8);
                        posts = feed.split("---\n");
                        // After refreshing, reset the index to show the current post again if it's not hidden
                        if (hiddenPostIds.contains(currentPostId) || hiddenUsernames.contains(postAuthor)) {
                            index++;
                        }
                    } else {
                        System.out.println(response);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Refresh error!");
                }
            }
        }
    }

    private void deleteAccount(Scanner scanner) {
        System.out.println();
        System.out.print("Are you sure you want to delete your account?: ");

        String choice = scanner.nextLine().trim();

        if (choice.equalsIgnoreCase("YES") || choice.equalsIgnoreCase("Y")) {
            sendRequest("DELETE_ACCOUNT");
            try {
                String response = (String) in.readObject();
                System.out.println(response.split("\\|")[1]);
                if (response.startsWith("SUCCESS")) {
                    isLoggedIn = false;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("\nError deleting account: " + e.getMessage());
            }
        }
    }

    // Allows users to view comments one at a time
    private void viewComments(Scanner scanner, String currentPostId) {
        try {
            sendRequest("VIEW_COMMENTS|" + currentPostId);
            String commentsResponse = (String) in.readObject();
            String[] comments = commentsResponse.split("---\n");

            if (commentsResponse.startsWith("FAILURE|")) {
                System.out.println(commentsResponse.split("\\|")[1]);
                return;
            }

            int index = 0;
            while (true) {
                if (index >= comments.length) {
                    System.out.println("End of comments.");
                    return;
                }
                String[] commentParts = comments[index].split("\n");
                String currentCommentId = commentParts[3].split(":")[1].trim();

                System.out.println();
                System.out.printf("=== Comment %d ===\n", index + 1);
                System.out.println("Comment ID: "  + currentCommentId);
                System.out.print(commentParts[0] + "\n");
                System.out.print(commentParts[1] + "\n");
                System.out.print(commentParts[2] + "\n");

                System.out.println("\n=== Comment Options === ");
                System.out.println("1. Upvote Comment");
                System.out.println("2. Downvote Comment");
                System.out.println("3. Next Comment");
                System.out.println("4. Return to Post Options");
                System.out.print("Choose an option: ");

                String choice = scanner.nextLine().trim();
                String request = "";

                switch (choice) {
                    case "1":
                        request = String.format("UPVOTE_COMMENT|%s|%s",
                                currentPostId, currentCommentId);
                        sendRequest(request);

                        try {
                            String response = (String) in.readObject();
                            System.out.println(response.split("\\|")[1]);

                            // Refreshes comments
                            sendRequest("VIEW_COMMENTS|" + currentPostId);
                            commentsResponse = (String) in.readObject();
                            comments = commentsResponse.split("---\n");

                        } catch (IOException | ClassNotFoundException e) {
                            System.out.println("\nError in upvoting comment!");
                        }

                        break;
                    case "2":
                        request = String.format("DOWNVOTE_COMMENT|%s|%s",
                                currentPostId, currentCommentId);
                        sendRequest(request);

                        try {
                            String response = (String) in.readObject();
                            System.out.println(response.split("\\|")[1]);

                            sendRequest("VIEW_COMMENTS|" + currentPostId);
                            commentsResponse = (String) in.readObject();
                            comments = commentsResponse.split("---\n");


                        } catch (IOException | ClassNotFoundException e) {
                            System.out.println("\nError in upvoting comment!");
                        }

                        break;
                    case "3":
                        index++;
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("\nInvalid choice. Please select a number from 1 to 4.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error viewing comments: " + e.getMessage());
        }
    }

    private void deletePost(Scanner scanner) {
        System.out.println();
        sendRequest("VIEW_SELF_POSTS");

        try {
            String response = (String) in.readObject();
            String[] parts = response.split("\\|");

            if (parts[0].equals("FAILURE") && parts[1].equals("No posts found.")) {
                System.out.println(parts[1]);
            } else if (parts[0].equals("SUCCESS")) {
                System.out.println("These are your posts (ID): \n" + parts[1]);
                System.out.print("Enter the ID of the post you want to delete: ");

                sendRequest("DELETE_POST|" + scanner.nextLine().trim());
                response = (String) in.readObject();
                response = response.split("\\|")[1];
                System.out.println(response);
            } else {
                System.out.println("\nError in deleting post: " + response.split("\\|")[1]);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nError deleting post: " + e.getMessage());
        }
    }

    private void searchUsers(Scanner scanner) {
        System.out.println();
        System.out.print("Enter username to search: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Please enter a valid username.");
            return;
        }
        sendRequest("SEARCH_USERS|" + username);
        try {
            String response = (String) in.readObject();
            System.out.println(response.contains("SUCCESS") ? response.substring(8) : response);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nError searching users: " + e.getMessage());
        }
    }

    private void logout() {
        isLoggedIn = false;
        loggedInUsername = null;
        System.out.println("\nLogged out successfully.");
    }

    private void uploadProfilePicture() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files",
                "jpg", "png", "jpeg");

        chooser.setDialogTitle("Select a profile image to upload");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(filter);
        int userSelection = chooser.showOpenDialog(null);
        String[] validExtensions = {".jpg", ".jpeg", ".png"};


        // Encodes image into a String
        if (userSelection == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
            File file = chooser.getSelectedFile();
            boolean valid = false;

            String fileNameExtension = file.getName().substring(file.getName().indexOf("."));

            for (String extension : validExtensions) {
                if (fileNameExtension.equalsIgnoreCase(extension)) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                System.out.println("\nInvalid file type! (Must end in .jpg, .jpeg, or .png)");
                return;
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] imageToByte = new byte[(int) file.length()];
                fis.read(imageToByte);

                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageToByte));
                if (image.getWidth() > 1000 || image.getHeight() > 1000) {
                    System.out.println("\nImage must be smaller than 1000x1000!");
                    return;
                }

                String imageString = Base64.getEncoder().encodeToString(imageToByte);

                sendRequest("UPLOAD_PROFILE_PICTURE|" + imageString);
                String response = (String) in.readObject();
                System.out.println(response.split("\\|")[1]);

            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                System.out.println("\nError uploading image file: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("\nInvalid image type!");
            }

        } else {
            System.out.println("\nNo file was selected!");
        }
    }

    private void viewPFP() {
        sendRequest("VIEW_PFP");
        try {
            String response = (String) in.readObject();

            if (response.startsWith("SUCCESS")) {
                String imageString = response.split("\\|")[1];

                byte[] imageToByte = Base64.getDecoder().decode(imageString);
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageToByte));

                JFrame imageFrame = new JFrame();
                imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ImageIcon imageIcon = new ImageIcon(image);

                JLabel label = new JLabel(imageIcon);

                label.setHorizontalAlignment(JLabel.CENTER);
                label.setVerticalAlignment(JLabel.CENTER);

                imageFrame.setLayout(new BorderLayout());
                imageFrame.add(label, BorderLayout.CENTER);

                imageFrame.pack();
                imageFrame.setVisible(true);

            } else {
                System.out.println(response.split("\\|")[1]);
            }

        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            System.out.println("\nError viewing PFP: " + e.getMessage());
        }

    }

    private void quit() {
        sendRequest("QUIT");
        try {
            in.close();
            out.close();
            socket.close();
            System.out.println("Exiting program...");
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    private void sendRequest(String request) {
        try {
            out.writeObject(request);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending request: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ClientMainMenu client = new ClientMainMenu("localhost", 12345);
        if (client.connectionTimeout) {
            return;
        } else {
            client.start();
        }
    }
}