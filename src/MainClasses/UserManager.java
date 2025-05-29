// Package
package MainClasses;

// Imports
import Interfaces.UserManagerInterface;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class UserManager implements UserManagerInterface {
    private static String userInfoFile = "DataBase/UserInfo.txt";
    private static String userDirectory = "DataBase/Users/";
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final Object fileLock = new Object();

    public UserManager() {
        createDirectories();
        loadUsers();
    }

    private UserManager(String userInfoFile2, String userDirectory2) { // For testing purposes
        userInfoFile = userInfoFile2;
        userDirectory = userDirectory2;
        createDirectories();
        loadUsers();
    }

    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get("DataBase"));
            Files.createDirectories(Paths.get(userDirectory));
            File userInformationFile = new File(UserManager.userInfoFile);
            if (!userInformationFile.exists()) {
                userInformationFile.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error creating directories: " + e.getMessage());
        }
    }

    public List<String> getAllUsernames() {
        return new ArrayList<>(users.keySet());
    }

    private void loadUsers() {
        synchronized (fileLock) {
            try (BufferedReader reader = new BufferedReader(new FileReader(userInfoFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        users.put(parts[1], parts[0]); // Username as key, userId as value
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading users: " + e.getMessage());
            }
        }
    }

    private void saveUserToInfoFile(String userId, String username) {
        synchronized (fileLock) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(userInfoFile, true))) {
                writer.write(userId + "," + username);
                writer.newLine();
            } catch (IOException e) {
                System.out.println("Error saving user to UserInfo.txt: " + e.getMessage());
            }
        }
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public boolean userIdExists(String userId) {
        return users.containsValue(userId);
    }

    public String registerUser(String name, String username, String password) {
        if (userExists(username)) {
//            System.out.println("Username already exists.");
            return "usernameError";
        } else if (!isValidPassword(password)) {
//            System.out.println("Password must be at least 8 characters long, " +
//                    "contain one uppercase letter, one digit, and one special character.");
            return "passwordError";
        }

        String userId = UUID.randomUUID().toString().substring(0, 13);
        User newUser = new User(userId, name, username, password);

        synchronized (fileLock) {
            users.put(username, userId);
        }

        saveUserToInfoFile(userId, username);
        newUser.saveUserToFile();
        return "success";
    }

    public boolean loginUser(String username, String password) {
        String userId = users.get(username);
        if (userId == null) {
            System.out.println("Username does not exist.");
            return false;
        }

        User user = new User(userId);
        if (user.getPassword().equals(password)) {
            System.out.println("Login successful.");
            return true;
        } else {
            System.out.println("Invalid password.");
            return false;
        }
    }

    public String getUserIdByUsername(String username) {
        return users.get(username);
    }

    public String getUsernameById(String userId) {
        return users.entrySet().stream()
                .filter(entry -> entry.getValue().equals(userId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public String getUserPosts(String userID) {
        StringBuilder posts = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("DataBase/Posts.txt"))) {
            String line = reader.readLine();

            while (line != null) {
                String userIDFromPost = line.split("\\|~\\|")[1];
                String postIDFromPost = line.split("\\|~\\|")[0];
                String contentFromPost = line.split("\\|~\\|")[2];
                boolean isImage = Boolean.parseBoolean(line.split("\\|~\\|")[5]);

                if (isImage && userIDFromPost.equals(userID)) {
                    posts.append(postIDFromPost).append(": ").append("IMAGE").append("\n");
                } else if (userIDFromPost.equals(userID)) {
                    posts.append(postIDFromPost).append(": ").append(contentFromPost).append("\n");
                }
                line = reader.readLine();
            }

        } catch (IOException e) {
            return "ERROR";
        }

        if (posts.length() == 0) {
            return "NO POSTS FOUND";
        }

        return posts.toString();
    }

    public boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern digit = Pattern.compile("[0-9]");
        Pattern specialChar = Pattern.compile("[!?@#$%^&*]");
        return uppercase.matcher(password).find() && digit.matcher(password).find()
                && specialChar.matcher(password).find();
    }

    public boolean deleteUser(String username) {
        String userId = users.remove(username);
        if (userId == null) {
            System.out.println("Prod.User not found.");
            return false;
        }

        saveUpdatedUserInfo();
        try {
            Files.deleteIfExists(Paths.get(userDirectory + userId + ".txt"));
            System.out.println("User deleted successfully.");
            return true;
        } catch (IOException e) {
            System.out.println("Error deleting user file: " + e.getMessage());
            return false;
        }
    }

    private void saveUpdatedUserInfo() {
        synchronized (fileLock) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(userInfoFile))) {
                for (Map.Entry<String, String> entry : users.entrySet()) {
                    writer.write(entry.getValue() + "," + entry.getKey());
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error updating UserInfo.txt: " + e.getMessage());
            }
        }
    }

    public void removeUsernameMapping(String username) {
        users.remove(username);
    }

    public void addUsernameMapping(String username, String userId) {
        users.put(username, userId);
    }

    public void saveUsers() {
        saveUpdatedUserInfo();
    }

    public List<String> getAllUserIds() {
        return new ArrayList<>(users.values());
    }
}