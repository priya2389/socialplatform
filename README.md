# Comprehensive Social Networking Platform

### About
The product is a robust, lightweight social networking platform designed to provide a streamlined and intuitive social media experience. Our primary goal is to create a modular, maintainable social networking solution that emphasizes user privacy, ease of use, and clean software design.

### Installation 

### Prerequisites
- Java Development Kit (JDK) 8 or higher.
- Basic command-line interface knowledge.

### Server Setup
```bash
- # Clone the repository
- git clone https://github.com/pate2389/socialplatform.git

- # Navigate to project directory
- cd socialplatform

- # Compile server components
- javac Prod/*.java

- # Start the server
- java Prod.ServerMainMenu

```

### Client Setup
# Launch client application
java Prod.ClientMainMenuGUI localhost 12345


---

## System Architecture
### Design Principles
- **Separation of Concerns:** Each component has a distinct, focused responsibility.
- **Modularity:** Organized packages and classes for easy maintenance.
- **Extensibility:** Designed to accommodate future feature additions.
- **Concurrency:** Thread-safe operations and efficient resource management.

### Technology Stack
- **Programming Language:** Java 8+
- **Architecture:** Client-Server Model
- **GUI Framework:** Java Swing
- **Concurrency:** Java Threading and Synchronization Mechanisms
- **Data Persistence:** File-based Storage System

---

## Features

### User Management
- Profile creation
- Password protected login
- Secure password/data storage
- Profile customization options (including profile pictures)
- Account creation, update, deletion

### Social Interactions
- Send, accept reject friend requests
- Search for users
- Blocking users and privacy controls
- 

### Content Management
- Text and image-based post creation
- Comment system
- Voting mechanism (upvote/downvote)
- Personalized user feed
- Content moderation tools

---

## System Components

### Server-Side 
- **ServerMainMenu:** Initializes the system, listens for incoming client connections, and manages a thread pool to handle each client concurrently. 
- **ClientHandler:** Manages communication with a single connected user. Reads client requests, delegates them to the appropriate manager classes for processing, and sends back responses.
- **UserManager:** Manages all user accounts, including registration, login, password validation, and user profile data persistence. It handles the creation, retrieval, and deletion of user records.
- **FriendRequestSystem:** Handles social connections between users. This includes sending, accepting, and declining friend requests, as well as managing user blocking functionality to control interactions and content visibility.
- **UserFeed:** Manages all user-generated content, including creating and storing posts (text and images) and comments. It also handles voting on content and enforces visibility rules, ensuring users only see relevant posts and comments from their friends or unblocked users.

### Client-Side 
- **ClientMainMenuGUI:** Main graphical user interface (GUI). Handles all user interactions, from initial server connection and account creation/login to navigating through the user menu, viewing profiles, and managing friend requests. It uses a CardLayout to switch between different screens and communicates with the server to send user requests and receive responses.
- **Rectangle:** A custom Swing JPanel component designed primarily for displaying images, typically used for user profile pictures (PFPs). It draws a basic rectangular frame and can display either a user-provided image or a default image if none is set. This class ensures images are scaled correctly within its bounds and provides basic error handling for missing default image files.

### Objects
- **Comment:** Represents a comment on a post, storing its ID, author, associated post, content, and vote counts. It handles comment creation, voting, and conversion for storage.
- **Post:** Defines a user-created post, including its unique ID, author, content (text or image), and vote counts. Manages a list of associated comments. Handles post creation, voting, commenting, and data persistence.
- **User:**  Stores an individual user's account information, such as name, username, password, and unique ID. Manages their friend list and blocked list. Handles user data persistence and modifications like changing usernames, passwords, or profile pictures.
---

## Data Management

### Data Storage Strategy
- Localized file-based storage.
- Synchronized file operations.
- Thread-safe data collections.

### File Structure
- `DataBase/UserInfo.txt`: User ID and username mappings.
- `DataBase/Users/`: Individual user data files.
- `DataBase/Posts.txt`: Post and comment storage.
- `DataBase/PendingFriendRequest.txt`: Friend request tracking.

---

### Contributors
- **Priya Patel**
- **Brandon Zhang**
- **Cheng-Kai Chiang**
- **Nila U Kumar**
- **Matthew Tomasz Galej**

