# Comprehensive Social Networking Platform

### About
The product is a robust, lightweight social networking platform designed to provide a streamlined and intuitive social media experience. Our primary goal is to create a modular, maintainable social networking solution that emphasizes user privacy, ease of use, and clean software design.

### Installation 
Download the icon used for a blank profile picture [click me to download](https://github.com/priya2389/socialplatform/blob/main/icon.png)
Run server class (ServerMainMenu)
Run client class (ClientMainMenuGUI)

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
- Text and image-based post creation.
- Comment system.
- Voting mechanism (upvote/downvote).
- Personalized user feed.
- Content moderation tools.

---

## System Components

### Server-Side Components
- **ServerMainMenu:** Entry point for server operations, handles connection management.
- **ClientHandler:** Manages communication with individual clients, processes requests.
- **UserManager:** Handles user registration, authentication, and data management.
- **FriendRequestSystem:** Manages friend relationships, processes requests, maintains social graph.
- **UserFeed:** Manages posts, comments, and interaction tracking.

### Client-Side Components
- **ClientMainMenuGUI:** Graphical user interface for navigating the platform.
- **Rectangle:** Profile picture rendering with consistent image presentation.

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

## Installation Guide

### Prerequisites
- Java Development Kit (JDK) 8 or higher.
- Basic command-line interface knowledge.

### Server Setup
```bash
- # Clone the repository
- git clone https://github.com/yourusername/BoilerFriends.git

- # Navigate to project directory
- cd BoilerFriends

- # Compile server components
- javac Prod/*.java

- # Start the server
- java Prod.ServerMainMenu

```

### Client Setup
# Launch client application
java Prod.ClientMainMenuGUI localhost 12345


### Contributors
- **Priya Patel**
- **Brandon Zhang**
- **Cheng-Kai Chiang**
- **Nila U Kumar**
- **Matthew Tomasz Galej**

