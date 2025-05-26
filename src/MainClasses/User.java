// Package
package MainClasses;

// Imports
import Interfaces.UserInterface;
import java.io.*;
import java.util.*;

public class User implements UserInterface {
    private String id;
    private String name;
    private String username;
    private String password;
    private Set<String> friendList;
    private Set<String> blockedList;
    private String profilePicture;

    public User(String id, String name, String username, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.profilePicture = "NO_PFP";
        this.friendList = new HashSet<>();
        this.blockedList = new HashSet<>();
        saveUserToFile();
    }

    public User(String id) {
        this.id = id;
        loadUserFromFile();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Set<String> getFriendList() { return friendList; }
    public Set<String> getBlockedList() { return blockedList; }
    public String getProfilePictureString() { return profilePicture; }

    public synchronized boolean changeUsername(String newUsername, UserManager userManager) {
        if (userManager.userExists(newUsername)) {
            return false;
        }
        // Remove old username mapping
        userManager.removeUsernameMapping(this.username);
        // Update username
        this.username = newUsername;
        // Add new username mapping
        userManager.addUsernameMapping(newUsername, this.id);
        saveUserToFile();
        // Save updated user info in Prod.UserManager
        userManager.saveUsers();

        return true;
    }

    public synchronized void changePassword(String newPassword) {
        this.password = newPassword;
        saveUserToFile();
    }

    public synchronized void changeProfilePicture(String newProfilePictureString) {
        this.profilePicture = newProfilePictureString;
        saveUserToFile();
    }

    public synchronized void saveUserToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("DataBase/Users/" + id + ".txt"))) {
            writer.write(String.join(",", id, name, username, password));
            writer.newLine();
            writer.write(String.join(",", friendList));
            writer.newLine();
            writer.write(String.join(",", blockedList));
            writer.newLine();
            writer.write(profilePicture);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    private void loadUserFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("DataBase/Users/" + id + ".txt"))) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Prod.User data is missing or improperly formatted.");
            }
            String[] info = line.split(",");
            if (info.length < 4) {
                throw new IOException("Incomplete user information.");
            }
            this.name = info[1];
            this.username = info[2];
            this.password = info[3];

            line = reader.readLine();
            this.friendList = line != null && !line.isEmpty() ? new HashSet<>(Arrays.asList(line.split(","))) :
                    new HashSet<>();

            line = reader.readLine();
            this.blockedList = line != null && !line.isEmpty() ? new HashSet<>(Arrays.asList(line.split(","))) :
                    new HashSet<>();

            line = reader.readLine();
            this.profilePicture = line;

        } catch (IOException e) {
            System.out.println("Error loading user: " + e.getMessage());
        }
    }
}