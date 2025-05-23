// Package
package MainClasses;

// Imports
import java.io.*;
import java.util.*;

public class FriendRequestSystem implements FriendInterface {
    private static final String PENDING_REQUEST_FILE = "DataBase/PendingFriendRequest.txt";
    private final UserManager userManager;
    private final Map<String, Set<String>> friendRequests = Collections.synchronizedMap(new HashMap<>());

    public FriendRequestSystem(UserManager userManager) {
        this.userManager = userManager;
        loadPendingRequests();
    }

    private synchronized void loadPendingRequests() {
        try (BufferedReader bfr = new BufferedReader(new FileReader(PENDING_REQUEST_FILE))) {
            String line;
            while ((line = bfr.readLine()) != null) {
                String[] parts = line.split(",");
                String userId = parts[0];
                friendRequests.putIfAbsent(userId, Collections.synchronizedSet(new HashSet<>()));
                for (int i = 1; i < parts.length; i++) {
                    friendRequests.get(userId).add(parts[i]);
                }
            }
        } catch (FileNotFoundException e) {
            try {
                new File(PENDING_REQUEST_FILE).createNewFile();
            } catch (IOException ex) {
                System.out.println("Error creating pending request file: " + ex.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error loading pending friend requests: " + e.getMessage());
        }
    }

    private synchronized void savePendingRequests() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PENDING_REQUEST_FILE))) {
            for (Map.Entry<String, Set<String>> entry : friendRequests.entrySet()) {
                String userId = entry.getKey();
                Set<String> requesters = entry.getValue();
                if (!requesters.isEmpty()) {
                    bw.write(userId + "," + String.join(",", requesters));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving pending requests: " + e.getMessage());
        }
    }

    public synchronized void sendFriendRequest(String userId, String friendId) {
        if (!userManager.userIdExists(friendId)) {
            System.out.println("Prod.User not found.");
            return;
        }
        if (isEitherBlocked(userId, friendId)) {
            System.out.println("Cannot send friend request as one user has blocked the other.");
            return;
        }
        friendRequests.putIfAbsent(friendId, Collections.synchronizedSet(new HashSet<>()));
        if (friendRequests.get(friendId).add(userId)) {
            System.out.println("Friend request sent.");
            savePendingRequests();
        } else {
            System.out.println("Friend request already sent.");
        }
    }

    public synchronized void acceptFriendRequest(String userId, String requesterId) {
        Set<String> requests = friendRequests.getOrDefault(userId, Collections.synchronizedSet(new HashSet<>()));
        if (!requests.remove(requesterId)) {
            System.out.println("No friend request from this user.");
            return;
        }
        friendRequests.put(userId, requests);

        User user = new User(userId);
        User requester = new User(requesterId);

        user.getFriendList().add(requesterId);
        requester.getFriendList().add(userId);

        user.saveUserToFile();
        requester.saveUserToFile();

        savePendingRequests();
        System.out.println("Friend request accepted.");
    }

    public synchronized void declineFriendRequest(String userId, String requesterId) {
        Set<String> requests = friendRequests.getOrDefault(userId, Collections.synchronizedSet(new HashSet<>()));
        if (!requests.remove(requesterId)) {
            System.out.println("No friend request from this user.");
            return;
        }
        friendRequests.put(userId, requests);
        savePendingRequests();
        System.out.println("Friend request declined.");
    }

    public synchronized void removeFriend(String userId, String friendId) {
        User user = new User(userId);
        User friend = new User(friendId);

        if (user.getFriendList().remove(friendId)) {
            friend.getFriendList().remove(userId);
            user.saveUserToFile();
            friend.saveUserToFile();
            System.out.println("Friend removed successfully.");
        } else {
            System.out.println("This user is not in your friend list.");
        }
    }

    public synchronized void blockUser(String userId, String blockId) {
        if (!userManager.userIdExists(blockId)) {
            System.out.println("Prod.User not found.");
            return;
        }

        User user = new User(userId);
        User blockedUser = new User(blockId);

        if (user.getFriendList().remove(blockId)) {
            blockedUser.getFriendList().remove(userId);
            blockedUser.saveUserToFile();
            System.out.println("Removed from friends as they are now blocked.");
        }

        if (user.getBlockedList().add(blockId)) {
            user.saveUserToFile();
            System.out.println("Prod.User blocked successfully.");
        } else {
            System.out.println("Prod.User is already blocked.");
        }
    }

    public synchronized void unblockUser(String userId, String unblockId) {
        User user = new User(userId);
        if (user.getBlockedList().remove(unblockId)) {
            user.saveUserToFile();
            System.out.println("Prod.User unblocked successfully.");
        } else {
            System.out.println("Prod.User is not in your blocked list.");
        }
    }

    public synchronized boolean isFriend(String userId, String otherUserId) {
        User user = new User(userId);
        return user.getFriendList().contains(otherUserId);
    }

    public synchronized boolean isBlocked(String userId, String otherUserId) {
        User user = new User(userId);
        return user.getBlockedList().contains(otherUserId);
    }

    public synchronized boolean isEitherBlocked(String userId, String otherUserId) {
        User user1 = new User(userId);
        User user2 = new User(otherUserId);
        return user1.getBlockedList().contains(otherUserId) || user2.getBlockedList().contains(userId);
    }

    public synchronized List<String> getPendingRequests(String userId) {
        return new ArrayList<>(friendRequests.getOrDefault(userId, Collections.emptySet()));
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public synchronized void removeUserFromPendingRequests(String userId) {
        friendRequests.remove(userId);
        for (Set<String> requesters : friendRequests.values()) {
            requesters.remove(userId);
        }
        savePendingRequests();
    }

    public List<String> searchUsersByUsername(String searchTerm) {
        List<String> matchedUsernames = new ArrayList<>();
        for (String username : userManager.getAllUsernames()) {
            if (username.toLowerCase().contains(searchTerm.toLowerCase())) {
                matchedUsernames.add(username);
            }
        }
        return matchedUsernames;
    }

    public synchronized void displayUserList() {
        System.out.println("=== All Users ===");
        for (String userId : userManager.getAllUserIds()) {
            User user = new User(userId);
            System.out.println(user.getUsername());
        }
    }
}