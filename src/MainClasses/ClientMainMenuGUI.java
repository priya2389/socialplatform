// Package
package MainClasses;

// Imports

import Interfaces.GuiInterface;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.*;

public class ClientMainMenuGUI extends JFrame implements GuiInterface {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isLoggedIn = false;
    private boolean isConnected = false;
    private boolean connectionTimeout = false;
    private String loggedInUsername;
    private Set<String> hiddenPostIds = new HashSet<>();
    private Set<String> hiddenUsernames = new HashSet<>();

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JLabel statusLabel;

    public ClientMainMenuGUI(String host, int port) {
        super("BoilerFriends");
        initializeGUI();

        int waitTimeMilliseconds = 5000;
        int timeOutMilliseconds = 60000;

        while (true) {
            if (waitTimeMilliseconds > timeOutMilliseconds) {
                JOptionPane.showMessageDialog(this,
                        "Server is unreachable at this time! Try again later.", "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                connectionTimeout = true;
                return;
            }
            try {
                socket = new Socket(host, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                statusLabel.setText("Connected to the server");
                statusLabel.setForeground(new Color(127, 200, 127));
                isConnected = true;
                break;
            } catch (IOException e) {
                try {
                    int msCount = 0;
                    while (msCount < waitTimeMilliseconds) {
                        statusLabel.setText(String.format("Unable to connect! Retrying in %s seconds...",
                                (waitTimeMilliseconds - msCount) / 1000));
                        statusLabel.setForeground(Color.RED);
                        Thread.sleep(1000);
                        msCount += 1000;
                    }
                    waitTimeMilliseconds += 5000;
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(this, "Retry was interrupted!",
                            "Interrupted", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1000, 600);
        setLocationRelativeTo(null);

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to apply Nimbus Look and Feel");
        }
        UIManager.put("nimbusFocus", Color.BLACK);

        // Sets the icon
        try {
            Image icon = ImageIO.read(new File("public/icon.png"));
            setIconImage(icon);
        } catch (IOException e) {
            System.err.println("Failed to load icon");
        }


        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        statusLabel = new JLabel("Initializing...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        mainPanel.add(getWelcomePanel(), "Welcome");
        mainPanel.add(getLoginPanel(), "Login");
        mainPanel.add(getRegisterPanel(), "Register");
        mainPanel.add(getUserMenuPanel(), "UserMenu");

        add(statusLabel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);

        cardLayout.show(mainPanel, "Welcome");
    }

    private JPanel getWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Welcome to BoilerFriends!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JButton createAccountButton = new JButton("Create Account");
        JButton loginButton = new JButton("Log In");
        JButton quitButton = new JButton("Quit");

        createAccountButton.setBackground(new Color(66, 133, 244));
        createAccountButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(66, 133, 244));
        loginButton.setForeground(Color.WHITE);
        quitButton.setBackground(new Color(219, 68, 55));
        quitButton.setForeground(Color.WHITE);

        createAccountButton.addActionListener(e -> {
            if (isConnected) {
                cardLayout.show(mainPanel, "Register");
                requestFocusInWindow();
            }
        });
        createAccountButton.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    createAccountButton.doClick();
                }
            }
        });
        loginButton.addActionListener(e -> {
            if (isConnected) {
                cardLayout.show(mainPanel, "Login");
                requestFocusInWindow();
            }
        });
        loginButton.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        });
        quitButton.addActionListener(e -> quit());
        quitButton.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    quitButton.doClick();
                }
            }
        });

        // Maps esc key to the back button
        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = panel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
        actionMap.put("exit", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit?", "Confirm Exit?", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    quitButton.doClick();
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(createAccountButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(quitButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);
        gbc.gridy++;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel getLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Log In");
        JButton backButton = new JButton("Back");

        loginButton.setBackground(new Color(66, 133, 244));
        loginButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.LIGHT_GRAY);

        loginButton.addActionListener(e -> {
            login(usernameField.getText(), new String(passwordField.getPassword()));
            requestFocusInWindow();
        });
        loginButton.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        });

        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Welcome");
            requestFocusInWindow();
        });
        backButton.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    backButton.doClick();
                }
            }
        });

        // Maps esc key to the back button
        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = panel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "homeMenu");
        actionMap.put("homeMenu", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Welcome");
                requestFocusInWindow();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);
        gbc.gridx++;
        panel.add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(passwordLabel, gbc);
        gbc.gridx++;
        panel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel getRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("Name:");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JTextField nameField = new JTextField(15);
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        registerButton.setBackground(new Color(66, 133, 244));
        registerButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.LIGHT_GRAY);

        registerButton.addActionListener(e -> createAccount(
                nameField.getText(),
                usernameField.getText(),
                new String(passwordField.getPassword())
        ));
        registerButton.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    registerButton.doClick();
                }
            }
        });
        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Welcome");
            requestFocusInWindow();
        });
        backButton.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    backButton.doClick();
                }
            }
        });

        // Maps esc key to the back button
        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = panel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "homeMenu");
        actionMap.put("homeMenu", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Welcome");
                requestFocusInWindow();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx++;
        panel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(usernameLabel, gbc);
        gbc.gridx++;
        panel.add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(passwordLabel, gbc);
        gbc.gridx++;
        panel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel getUserMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setLayout(new GridLayout(0, 1, 10, 10));

        JButton viewProfileButton = new JButton("View Profile");
        JButton viewFriendRequestsButton = new JButton("View Friend Requests");
        JButton blockUnblockUserButton = new JButton("Block/Unblock User");
        JButton createPostButton = new JButton("Create Post");
        JButton viewFeedButton = new JButton("View Feed");
        JButton deleteAccountButton = new JButton("Delete Account");
        JButton searchUsersButton = new JButton("Search Users");
        JButton hideUserPostsButton = new JButton("Hide User Posts");
        JButton logoutButton = new JButton("Log Out");

        JButton[] buttons = {viewProfileButton, searchUsersButton, viewFriendRequestsButton, blockUnblockUserButton,
                createPostButton, hideUserPostsButton, viewFeedButton, deleteAccountButton, logoutButton};

        for (JButton button : buttons) {
            button.setBackground(new Color(66, 133, 244));
            if (button.getText().equals("Log Out") || button.getText().equals("Delete Account")) {
                button.setBackground(new Color(219, 68, 55));
            } else if (button.getText().equals("View Feed")) {
                button.setBackground(new Color(33, 163, 65));
            }

            button.setForeground(Color.WHITE);
            menuPanel.add(button);
            button.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        button.doClick();
                        requestFocusInWindow();
                    }
                }
            });
        }

        viewProfileButton.addActionListener(e -> {
            viewProfile();
            requestFocusInWindow();
        });
        viewFriendRequestsButton.addActionListener(e -> {
            viewFriendRequests();
            requestFocusInWindow();
        });
        blockUnblockUserButton.addActionListener(e -> {
            blockUnblockUser();
            requestFocusInWindow();
        });
        createPostButton.addActionListener(e -> {
            createPost();
            requestFocusInWindow();
        });
        viewFeedButton.addActionListener(e -> {
            viewFeed();
            requestFocusInWindow();
        });
        deleteAccountButton.addActionListener(e -> {
            deleteAccount();
            requestFocusInWindow();
        });
        searchUsersButton.addActionListener(e -> {
            searchUsers();
            requestFocusInWindow();
        });
        hideUserPostsButton.addActionListener(e -> {
            hidePost();
            requestFocusInWindow();
        });
        logoutButton.addActionListener(e -> {
            logout();
            cardLayout.show(mainPanel, "Welcome");
            requestFocusInWindow();
        });

        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = panel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
        actionMap.put("exit", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to logout?", "Confirm Exit?", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    logoutButton.doClick();
                }
            }
        });

        panel.add(new JScrollPane(menuPanel), BorderLayout.CENTER);
        return panel;
    }

    private void createAccount(String name, String username, String password) {
        if (name.length() >= 30 || username.length() >= 30) {
            JOptionPane.showMessageDialog(this,
                    "Username/name cannot be longer than 30 characters!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (name == null || name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (password == null || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        try {
            sendRequest("REGISTER|" + name + "|" + username + "|" + password);
            String response = (String) in.readObject();
            if (response.contains("SUCCESS")) {
                JOptionPane.showMessageDialog(this, "Account created successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "Login");
            } else {
                JOptionPane.showMessageDialog(this, response.split("\\|")[1],
                        "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error creating account: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void login(String username, String password) {
        try {
            sendRequest("LOGIN|" + username + "|" + password);
            String response = (String) in.readObject();
            if (response.startsWith("SUCCESS")) {
                isLoggedIn = true;
                loggedInUsername = username;
                statusLabel.setForeground(Color.BLACK);
                statusLabel.setText("Logged in as: " + loggedInUsername);
                cardLayout.show(mainPanel, "UserMenu");
            } else {
                JOptionPane.showMessageDialog(this, response.split("\\|")[1], "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error logging in: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewProfile() {
        sendRequest("VIEW_PROFILE");
        try {
            String response = (String) in.readObject();

            if (response.startsWith("FAILURE")) {
                JOptionPane.showMessageDialog(this, response.split("\\|")[1], "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(Color.WHITE);
            JPanel textWrapper = new JPanel(new BorderLayout());
            textWrapper.setBackground(Color.WHITE);
            JPanel textPanel = new JPanel(new GridBagLayout());
            textPanel.setBackground(Color.WHITE);
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.NORTHWEST;
            c.weightx = 1;
            c.weighty = 0;
            c.insets = new Insets(10, 10, 10, 0);

            // variables associated with server response
            String[] info = response.split("\\|")[1].split("\n");
            String name = info[0];
            String username = info[1];
            String[] friends;
            String[] blockedUsers;
            StringBuilder friendsList = new StringBuilder("\n");
            StringBuilder blockedUsersList = new StringBuilder("\n");

            try {
                friends = info[2].split(",");

            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                friends = new String[]{"No Friends :("};
            }

            for (String friend : friends)
                friendsList.append(friend).append(",");
            friendsList.deleteCharAt(friendsList.length() - 1);

            try {
                blockedUsers = info[3].split(",");
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                blockedUsers = new String[]{"No Blocked Users :)"};
            }

            for (String blockedUser : blockedUsers)
                blockedUsersList.append(blockedUser).append(",");
            blockedUsersList.deleteCharAt(blockedUsersList.length() - 1);

            JLabel nameLabel = new JLabel("Name: " + name);
            JLabel usernameLabel = new JLabel("Username: " + username);
            JTextArea friendsLabel = new JTextArea("Friends: " + friendsList.toString());
            JTextArea blockedLabel = new JTextArea("Blocked Users: " + blockedUsersList.toString());

            JTextArea[] labels = {friendsLabel, blockedLabel};

            for (JTextArea label : labels) {
                label.setEditable(false);
                label.setBackground(Color.WHITE);
                label.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                label.setOpaque(false);
                label.setWrapStyleWord(false);
                label.setLineWrap(true);
                label.setFocusable(false);
                label.setColumns(50);
            }

            JPanel bottomButtons = new JPanel(new FlowLayout());
            JButton changeUsernameButton = new JButton("Change Username");
            JButton changePasswordButton = new JButton("Change Password");
            JButton uploadPFPButton = new JButton("Upload Profile Picture");
            JButton deletePFPButton = new JButton("Delete Profile Picture");
            JButton backButton = new JButton("Back");

            JButton[] buttons = {changeUsernameButton, changePasswordButton, uploadPFPButton, deletePFPButton,
                    backButton};

            backButton.addActionListener(e -> {
                cardLayout.show(mainPanel, "UserMenu");
                requestFocusInWindow();
            });

            // Maps esc key to the back button
            InputMap inputMap = wrapper.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = wrapper.getActionMap();

            inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "mainMenu");
            actionMap.put("mainMenu", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainPanel, "UserMenu");
                    requestFocusInWindow();
                }
            });

            for (JButton button : buttons) {
                button.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            button.doClick();
                            requestFocusInWindow();
                        }
                    }
                });
                bottomButtons.add(button);
            }

            c.gridy = 0;
            textPanel.add(nameLabel, c);
            c.gridy++;
            textPanel.add(usernameLabel, c);
            c.gridy++;
            textPanel.add(friendsLabel, c);
            c.gridy++;
            textPanel.add(blockedLabel, c);

            Rectangle pfpFrame = new Rectangle(256, 256);
            JPanel pfpWrapper = new JPanel(new GridBagLayout());
            pfpWrapper.setBackground(Color.WHITE);

            sendRequest("VIEW_PFP");
            try {
                String imageResponse = (String) in.readObject();
                if (imageResponse.startsWith("SUCCESS")) {
                    String imageString = imageResponse.split("\\|")[1];
                    byte[] imageToByte = Base64.getDecoder().decode(imageString);
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageToByte));
                    pfpFrame.setImage(image);
                }
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                        "Error viewing profile picture: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }


            GridBagConstraints c2 = new GridBagConstraints();
            c2.anchor = GridBagConstraints.NORTHEAST;
            c2.gridy = 0;
            c2.weightx = 1;
            c2.weighty = 1;
            c2.insets = new Insets(10, 0, 0, 10);
            pfpWrapper.add(pfpFrame, c2);
            textWrapper.add(textPanel, BorderLayout.NORTH);

            wrapper.add(textWrapper, BorderLayout.WEST);
            wrapper.add(pfpWrapper, BorderLayout.EAST);
            wrapper.add(bottomButtons, BorderLayout.SOUTH);

            changeUsernameButton.addActionListener(e -> {
                String newUsername = JOptionPane.showInputDialog(this,
                        "Enter your new username:");
                if (newUsername != null && !newUsername.trim().isEmpty()) {
                    sendRequest("CHANGE_USERNAME|" + newUsername.trim());
                    try {
                        String resp = (String) in.readObject();
                        JOptionPane.showMessageDialog(this, resp.split("\\|")[1]);
                        if (resp.startsWith("SUCCESS")) {
                            loggedInUsername = newUsername.trim();
                            statusLabel.setText("Logged in as: " + loggedInUsername);
                            viewProfile();
                        }
                    } catch (IOException | ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Error changing username: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Username cannot be blank", "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });

            changePasswordButton.addActionListener(e -> {
                JPanel passPanel = new JPanel(new GridLayout(0, 2));
                JPasswordField currentPass = new JPasswordField();
                JPasswordField newPass = new JPasswordField();
                passPanel.add(new JLabel("Current Password:"));
                passPanel.add(currentPass);
                passPanel.add(new JLabel("New Password:"));
                passPanel.add(newPass);

                int option = JOptionPane.showConfirmDialog(this, passPanel,
                        "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    sendRequest("CHANGE_PASSWORD|" + new String(currentPass.getPassword()) +
                            "|" + new String(newPass.getPassword()));
                    try {
                        String resp = (String) in.readObject();
                        JOptionPane.showMessageDialog(this, resp.split("\\|")[1]);
                    } catch (IOException | ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Error changing password: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            uploadPFPButton.addActionListener(e -> {
                uploadProfilePicture();
                viewProfile();
            });

            deletePFPButton.addActionListener(e -> {
                sendRequest("UPLOAD_PROFILE_PICTURE|" + "NO_PFP");
                try {
                    String deleteResponse = (String) in.readObject();
                    if (deleteResponse.startsWith("SUCCESS")) {
                        JOptionPane.showMessageDialog(this, deleteResponse.split("\\|")[1]);
                        viewProfile();
                    } else {
                        JOptionPane.showMessageDialog(this, deleteResponse.split("\\|")[1],
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (IOException | ClassNotFoundException e2) {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting image file: " + e2.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            backButton.addActionListener(e -> {
                cardLayout.show(mainPanel, "UserMenu");
            });

            // Shows the card
            mainPanel.add(wrapper, "UserProfile");
            cardLayout.show(mainPanel, "UserProfile");

        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Error viewing profile: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewFriendRequests() {
        sendRequest("VIEW_FRIEND_REQUESTS");
        try {
            String response = (String) in.readObject();
            String[] parts = response.split("\\|");

            if (parts[0].contains("SUCCESS") && parts[1].contains("No pending friend requests.")) {
                JOptionPane.showMessageDialog(this, parts[1]);
            } else if (parts[0].contains("SUCCESS")) {
                String[] requests = parts[1].split("\n");
                int index = 0;

                while (true) {
                    if (index >= requests.length) {
                        JOptionPane.showMessageDialog(this, "End of friend requests.");
                        return;
                    }

                    String requestUser = requests[index];
                    int option = JOptionPane.showOptionDialog(this,
                            "Friend request from: " + requestUser,
                            "Friend Request",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                            new String[]{"Accept", "Decline", "Next", "Cancel"}, "Accept");

                    switch (option) {
                        case 0:
                            sendRequest("ACCEPT_FRIEND_REQUEST|" + requestUser);
                            response = (String) in.readObject();
                            JOptionPane.showMessageDialog(this, response.split("\\|")[1]);
                            index++;
                            break;
                        case 1:
                            sendRequest("DECLINE_FRIEND_REQUEST|" + requestUser);
                            response = (String) in.readObject();
                            JOptionPane.showMessageDialog(this, response.split("\\|")[1]);
                            index++;
                            break;
                        case 2:
                            index++;
                            break;
                        case 3:
                            return;
                        default:
                            return;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, parts[1], "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Error viewing friend requests: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void blockUnblockUser() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel wrapper = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Enter the username of the person:");
        JTextField input = new JTextField(panel.getWidth());
        JLabel bottomLabel = new JLabel("Select an action");
        JComboBox<String> actions = new JComboBox<>(new String[]{"Block", "Unblock"});

        panel.add(label, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        wrapper.add(bottomLabel, BorderLayout.NORTH);
        wrapper.add(actions, BorderLayout.CENTER);
        panel.add(wrapper, BorderLayout.SOUTH);

        int choice = JOptionPane.showOptionDialog(null, panel, "Block/Unblock",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

        if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        String username = input.getText().trim();

        if (username != null && !username.isEmpty()) {
            String actionCommand = (String) actions.getSelectedItem();
            switch (actionCommand) {
                case "Block":
                    sendRequest("BLOCK_USER|" + username);
                    try {
                        String response = (String) in.readObject();
                        JOptionPane.showMessageDialog(this, response.split("\\|")[1]);
                    } catch (IOException | ClassNotFoundException e) {
                        JOptionPane.showMessageDialog(this,
                                "Error blocking user: " + e.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case "Unblock":
                    sendRequest("UNBLOCK_USER|" + username.trim());
                    try {
                        String response = (String) in.readObject();
                        JOptionPane.showMessageDialog(this, response.split("\\|")[1]);
                    } catch (IOException | ClassNotFoundException e) {
                        JOptionPane.showMessageDialog(this,
                                "Error unblocking user: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                default:
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Input cannot be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createPost() {
        String[] options = {"Image Post", "Text Post", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
                "What type of post would you like to create?", "Create Post", JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        String content = "";
        switch (choice) {
            case 0:
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Image files", "jpg", "png", "jpeg");
                chooser.setFileFilter(filter);
                chooser.setAcceptAllFileFilterUsed(false);
                int userSelection = chooser.showOpenDialog(this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] imageToByte = new byte[(int) file.length()];
                        fis.read(imageToByte);
                        String imageString = Base64.getEncoder().encodeToString(imageToByte);
                        content = imageString;
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this,
                                "Error encoding image file: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    sendRequest("CREATE_POST_IMAGE|" + content);
                    try {
                        String response = (String) in.readObject();
                        JOptionPane.showMessageDialog(this, response.split("\\|")[1]);
                    } catch (IOException | ClassNotFoundException e) {
                        JOptionPane.showMessageDialog(this,
                                "Error creating post: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No file selected.");
                    return;
                }
                break;
            case 1:
                content = JOptionPane.showInputDialog(this, "Enter the content of your post:");
                if (content == null || content.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Post content cannot be empty!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                sendRequest("CREATE_POST|" + content);
                try {
                    String response = (String) in.readObject();
                    JOptionPane.showMessageDialog(this, response.split("\\|")[1]);
                } catch (IOException | ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(this,
                            "Error creating post: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 2:
            default:
                return;
        }
    }

    private void viewFeed() {
        sendRequest("VIEW_FEED");
        try {
            String response = (String) in.readObject();
            if (response.startsWith("SUCCESS")) {
                displayPosts(response.substring(8));
            } else {
                JOptionPane.showMessageDialog(this, response.split("\\|")[1]);
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Error viewing feed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayPosts(String feedContent) {
        String feed = feedContent;
        String[] posts = feed.split("---\n");
        int index = 0;

        while (true) {
            if (index >= posts.length) {
                JOptionPane.showMessageDialog(this, "End of posts.");
                return;
            }
            String postContent = posts[index];
            String currentPostId = null;
            String postAuthor = null;
            String postContentData = null;
            boolean isImage = false;
            String imageString = null;

            if (postContent.contains("Post ID:")) {
                currentPostId = postContent.split("Post ID: ")[1].split("\n")[0];
            }
            if (postContent.contains("Author:")) {
                postAuthor = postContent.split("Author: ")[1].split("\n")[0];
            }
            if (postContent.contains("Content:")) {
                postContentData = postContent.split("Content: ")[1].split("\n")[0];
            }
            if (postContent.contains("IMAGE")) {
                isImage = true;
                postContentData = "IMAGE";
                imageString = postContent.split("Content: ")[1].split("\n")[0].split(";")[1];
            }
            if (hiddenPostIds.contains(currentPostId) || hiddenUsernames.contains(postAuthor)) {
                index++;
                continue;
            }

            JPanel postPanel = new JPanel(new BorderLayout());

            JTextArea authorLabel = new JTextArea(String.format("Author: %s | Post ID: %s", postAuthor, currentPostId));
            authorLabel.setFont(new Font("Arial", Font.BOLD, 14));
            authorLabel.setFocusable(true);
            authorLabel.setEditable(false);
            authorLabel.setBackground(new Color(214, 217, 223));
            authorLabel.setBorder(BorderFactory.createLineBorder(new Color(214, 217, 223)));

            postPanel.add(authorLabel, BorderLayout.NORTH);

            if (isImage) {
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(imageString);
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                    ImageIcon imageIcon = new ImageIcon(image);
                    JLabel imageLabel = new JLabel(imageIcon);
                    JScrollPane scrollPane = new JScrollPane(imageLabel);
                    postPanel.add(scrollPane, BorderLayout.CENTER);
                } catch (Exception e) {
                    JTextArea postArea = new JTextArea("Error displaying image.");
                    postArea.setEditable(false);
                    postPanel.add(postArea, BorderLayout.CENTER);
                }
            } else {
                JTextArea postArea = new JTextArea(postContentData);
                postArea.setRows(2);
                postArea.setEditable(false);
                postArea.setLineWrap(true);
                postArea.setWrapStyleWord(true);
                JScrollPane scrollPane = new JScrollPane(postArea);
                postPanel.add(scrollPane, BorderLayout.CENTER);
            }

            String upvotes = postContent.split("\n")[3].split("&")[0];
            String downvotes = postContent.split("\n")[3].split("&")[1];
            JLabel votesLabel = new JLabel(String.format("Upvotes: %s | Downvotes: %s", upvotes, downvotes));
            postPanel.add(votesLabel, BorderLayout.SOUTH);

            int option = JOptionPane.showOptionDialog(this,
                    postPanel, "Post", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    new String[]{"Upvote", "Downvote", "Add Comment",
                            "View Comments", "Next Post", "Delete Post", "Back"}, 6);

            switch (option) {
                case 0:
                    sendRequest("UPVOTE_POST|" + currentPostId);
                    try {
                        String resp = (String) in.readObject();
                        sendRequest("VIEW_FEED");
                        String response = (String) in.readObject();
                        if (response.startsWith("SUCCESS")) {
                            feed = response.substring(8);
                            posts = feed.split("---\n");
                            if (hiddenPostIds.contains(currentPostId) || hiddenUsernames.contains(postAuthor)) {
                                index++;
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        JOptionPane.showMessageDialog(this,
                                "Error upvoting post: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 1:
                    sendRequest("DOWNVOTE_POST|" + currentPostId);
                    try {
                        String resp = (String) in.readObject();
                        sendRequest("VIEW_FEED");
                        String response = (String) in.readObject();
                        if (response.startsWith("SUCCESS")) {
                            feed = response.substring(8);
                            posts = feed.split("---\n");
                            if (hiddenPostIds.contains(currentPostId) || hiddenUsernames.contains(postAuthor)) {
                                index++;
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        JOptionPane.showMessageDialog(this,
                                "Error downvoting post: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 2:
                    String comment = JOptionPane.showInputDialog(this, "Enter your comment:");
                    if (comment != null && !comment.trim().isEmpty()) {
                        sendRequest("ADD_COMMENT|" + currentPostId + "|" + comment.trim());
                        try {
                            String resp = (String) in.readObject();
                            JOptionPane.showMessageDialog(this, resp.split("\\|")[1]);
                        } catch (IOException | ClassNotFoundException e) {
                            JOptionPane.showMessageDialog(this,
                                    "Error adding comment: " + e.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;
                case 3:
                    viewComments(currentPostId);
                    break;
                case 4:
                    index++;
                    break;
                case 5:
                    sendRequest("DELETE_POST|" + currentPostId);
                    try {
                        String resp = (String) in.readObject();
                        if (resp.startsWith("SUCCESS")) {
                            JOptionPane.showMessageDialog(this,
                                    "Sucessfully deleted post!", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            index++;
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Error deleting post: " + resp.split("\\|")[1], "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        JOptionPane.showMessageDialog(this,
                                "Error deleting post: " + e.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        index++;
                        break;
                    }
                    break;
                case 6:
                default:
                    return;
            }
        }
    }

    private void deleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account?",
                "Confirm Delete?", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            sendRequest("DELETE_ACCOUNT");
            try {
                String response = (String) in.readObject();
                JOptionPane.showMessageDialog(this, response.split("\\|")[1]);
                if (response.startsWith("SUCCESS")) {
                    isLoggedIn = false;
                    cardLayout.show(mainPanel, "Welcome");
                }
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting account: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewComments(String currentPostId) {
        try {
            sendRequest("VIEW_COMMENTS|" + currentPostId);
            String commentsResponse = (String) in.readObject();
            String[] comments = commentsResponse.split("---\n");

            if (commentsResponse.startsWith("FAILURE|")) {
                JOptionPane.showMessageDialog(this,
                        "Error in viewing comments: " + commentsResponse.split("\\|")[1],
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int index = 0;
            while (true) {
                if (index >= comments.length) {
                    JOptionPane.showMessageDialog(this, "End of comments.");
                    return;
                }
                String[] commentParts = comments[index].split("\n");
                String currentCommentContent = commentParts[1].split(":")[1].trim();
                String currentCommentId = commentParts[3].split(":")[1].trim();
                String postAuthor = commentParts[0].split(":")[1].trim();

                JTextArea commentArea = new JTextArea(currentCommentContent, 5, 1);
                commentArea.setEditable(false);
                commentArea.setLineWrap(true);
                commentArea.setWrapStyleWord(true);
                commentArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
                JScrollPane scrollPane = new JScrollPane(commentArea);

                JLabel authorLabel = new JLabel("Author: " + postAuthor);
                authorLabel.setFont(new Font("Arial", Font.BOLD, 14));

                JPanel commentPanel = new JPanel(new BorderLayout());
                commentPanel.add(authorLabel, BorderLayout.NORTH);
                commentPanel.add(scrollPane, BorderLayout.CENTER);

                String upvotes = commentParts[2].split("&")[0];
                String downvotes = commentParts[2].split("&")[1];
                JLabel votesLabel = new JLabel(String.format("Upvotes: %s | Downvotes: %s", upvotes, downvotes));
                commentPanel.add(votesLabel, BorderLayout.SOUTH);

                int option = JOptionPane.showOptionDialog(this, commentPanel,
                        "Comment", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                        new String[]{"Upvote", "Downvote", "Next Comment", "Back"}, "Upvote");

                switch (option) {
                    case 0:
                        sendRequest("UPVOTE_COMMENT|" + currentPostId + "|" + currentCommentId);
                        try {
                            String resp = (String) in.readObject();
                            sendRequest("VIEW_COMMENTS|" + currentPostId);
                            commentsResponse = (String) in.readObject();
                            comments = commentsResponse.split("---\n");
                        } catch (IOException | ClassNotFoundException e) {
                            JOptionPane.showMessageDialog(this,
                                    "Error upvoting comment: " + e.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case 1:
                        sendRequest("DOWNVOTE_COMMENT|" + currentPostId + "|" + currentCommentId);
                        try {
                            String resp = (String) in.readObject();
                            sendRequest("VIEW_COMMENTS|" + currentPostId);
                            commentsResponse = (String) in.readObject();
                            comments = commentsResponse.split("---\n");
                        } catch (IOException | ClassNotFoundException e) {
                            JOptionPane.showMessageDialog(this,
                                    "Error downvoting comment: " + e.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case 2:
                        index++;
                        break;
                    case 3:
                    default:
                        return;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Error viewing comments: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchUsers() {
        String username = JOptionPane.showInputDialog(this, "Enter username to search:");

        if (username == null) {
            return;
        } else if (!username.trim().isEmpty()) {
            sendRequest("SEARCH_USERS|" + username.trim());
            try {
                String response = (String) in.readObject();

                if (response.startsWith("SUCCESS")) {
                    String[] users = response.split("\\|")[1].split("\n");
                    boolean invalid = (users.length == 1) &&
                            users[0].equals("No users found matching the search term.");

                    JPanel panel = new JPanel(new BorderLayout());
                    JTextArea results = new JTextArea(String.join("\n", users), 3, 5);
                    results.setBackground(Color.WHITE);
                    results.setFocusable(false);
                    results.setEditable(false);
                    results.setLineWrap(true);
                    results.setWrapStyleWord(true);
                    results.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    JScrollPane scrollPane = new JScrollPane(results);

                    String[] userChoice = new String[users.length];

                    if (invalid) {
                        userChoice = new String[]{""};
                    } else {
                        for (int i = 0; i < users.length; i++) {
                            int index = users[i].indexOf(":");
                            userChoice[i] = users[i].substring(index + 2).trim(); // Omits the space
                        }
                    }

                    JComboBox<String> userDropdown = new JComboBox<>(userChoice);

                    panel.add(scrollPane, BorderLayout.CENTER);

                    JPanel wrapper = new JPanel(new BorderLayout());
                    JLabel label = new JLabel("Select a User:");
                    label.setFont(new Font("Arial", Font.BOLD, 12));
                    wrapper.add(label, BorderLayout.NORTH);
                    wrapper.add(userDropdown, BorderLayout.CENTER);
                    panel.add(wrapper, BorderLayout.SOUTH);

                    int choice = JOptionPane.showOptionDialog(this, panel, "Search results",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                            new String[]{"Add Friend", "Block User", "Back"}, 3);

                    String usernameString = (String) userDropdown.getSelectedItem();

                    if (choice == JOptionPane.CLOSED_OPTION)
                        return;

                    switch (choice) {
                        case 0:
                            if (!invalid) {
                                sendRequest("ADD_FRIEND|" + usernameString);
                                try {
                                    String resp = (String) in.readObject();
                                    JOptionPane.showMessageDialog(this, resp.split("\\|")[1]);
                                } catch (IOException | ClassNotFoundException e) {
                                    JOptionPane.showMessageDialog(this, "Error adding friend: "
                                            + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(this, "No user to be added!",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        case 1:
                            if (!invalid) {
                                sendRequest("BLOCK_USER|" + usernameString);
                                try {
                                    String resp = (String) in.readObject();
                                    JOptionPane.showMessageDialog(this, resp.split("\\|")[1]);
                                } catch (IOException | ClassNotFoundException e) {
                                    JOptionPane.showMessageDialog(this,
                                            "Error blocking user: " + e.getMessage(), "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(this, "No user to be blocked!",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        case 2:
                            return;
                        default:
                            break;
                    }
                } else {
                    String message = response.split("\\|")[1].trim();
                    JOptionPane.showMessageDialog(this, "Error with search: " + message,
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                        "Error searching users: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Input cannot be blank", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hidePost() {
        String[] options = {"Hide/unhide a post by ID", "Hide/unhide all posts from a user by " +
                "username", "Back"};

        JPanel panel = new JPanel(new BorderLayout());
        JLabel actionLabel = new JLabel("Chose an action");
        JComboBox<String> action = new JComboBox<>(new String[]{"Hide", "Unhide"});

        panel.add(actionLabel, BorderLayout.CENTER);
        panel.add(action, BorderLayout.SOUTH);

        int panelChoice = JOptionPane.showOptionDialog(this, panel, "Hide/Unhide Post",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, 2);

        switch (panelChoice) {
            case 0:
                String choice = (String) action.getSelectedItem();
                switch (choice) {
                    case "Hide":
                        String postId = JOptionPane.showInputDialog(this,
                                "Enter the Post ID to hide:");
                        if (postId != null && !postId.trim().isEmpty()) {
                            hiddenPostIds.add(postId.trim());
                            JOptionPane.showMessageDialog(this, "Post has been hidden.");
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Post ID cannot be empty. Please try again.");
                        }
                        break;
                    case "Unhide":
                        String id = JOptionPane.showInputDialog(this,
                                "Enter the Post ID to unhide:");
                        if (id != null && !id.trim().isEmpty()) {
                            if (hiddenPostIds.contains(id)) {
                                hiddenPostIds.remove(id);
                                JOptionPane.showMessageDialog(this, "Post has been unhidden.");
                            } else {
                                JOptionPane.showMessageDialog(this,
                                        "Hidden post could not be found", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Post ID cannot be empty. Please try again.");
                        }
                        break;
                }
                break;
            case 1:
                String choiceTwo = (String) action.getSelectedItem();
                switch (choiceTwo) {
                    case "Hide":
                        String username = JOptionPane.showInputDialog(this,
                                "Enter the username to hide all their posts:");
                        if (username != null && !username.trim().isEmpty()) {
                            if (username.equalsIgnoreCase(loggedInUsername)) {
                                JOptionPane.showMessageDialog(this,
                                        "You cannot hide your own posts.");
                            } else {
                                hiddenUsernames.add(username.trim());
                                JOptionPane.showMessageDialog(this,
                                        "All posts from the user have been hidden.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Username cannot be empty. Please try again.");
                        }
                        break;
                    case "Unhide":
                        String usernameUnhide = JOptionPane.showInputDialog(this,
                                "Enter the username to unhide all their posts:");
                        if (usernameUnhide != null && !usernameUnhide.trim().isEmpty()) {
                            if (usernameUnhide.equalsIgnoreCase(loggedInUsername)) {
                                JOptionPane.showMessageDialog(this,
                                        "You cannot unhide your own posts.");
                            } else {
                                if (hiddenUsernames.contains(usernameUnhide)) {
                                    hiddenUsernames.remove(usernameUnhide.trim());
                                    JOptionPane.showMessageDialog(this,
                                            "All posts from the user have been unhidden.");
                                } else {
                                    JOptionPane.showMessageDialog(this,
                                            "No user could not be found!", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Username cannot be empty. Please try again.");
                        }
                        break;
                }
            case 2:
            default:
                return;
        }
    }

    private void logout() {
        isLoggedIn = false;
        loggedInUsername = null;
        statusLabel.setText("Logged out");
    }

    private void uploadProfilePicture() {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setDialogTitle("Select a profile image to upload");
        chooser.setVisible(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image files", "jpg", "png", "jpeg");
        chooser.setFileFilter(filter);
        int userSelection = chooser.showOpenDialog(this);
        String[] validExtensions = {".jpg", ".jpeg", ".png"};

        if (userSelection == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
            File file = chooser.getSelectedFile();
            boolean valid = false;

            String fileNameExtension = file.getName().substring(file.getName().lastIndexOf("."));

            for (String extension : validExtensions) {
                if (fileNameExtension.equalsIgnoreCase(extension)) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                JOptionPane.showMessageDialog(this,
                        "Invalid file type!\nMust end in .jpg, .jpeg, or .png",
                        "Invalid File", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] imageToByte = new byte[(int) file.length()];
                fis.read(imageToByte);

                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageToByte));
                if (image.getWidth() > 1000 || image.getHeight() > 1000) {
                    JOptionPane.showMessageDialog(this,
                            "Image must be smaller than 1000x1000!",
                            "Invalid Image Size", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String imageString = Base64.getEncoder().encodeToString(imageToByte);

                sendRequest("UPLOAD_PROFILE_PICTURE|" + imageString);
                String response = (String) in.readObject();
                JOptionPane.showMessageDialog(this, response.split("\\|")[1]);

            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                        "Error uploading image file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid image type!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private void quit() {
        sendRequest("QUIT");
        try {
            in.close();
            out.close();
            socket.close();
            statusLabel.setText("Exiting...");
            System.exit(0);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error closing connection: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendRequest(String request) {
        try {
            out.writeObject(request);
            out.flush();
        } catch (IOException e) {
            if (request.equals("QUIT"))
                System.exit(0);

            JOptionPane.showMessageDialog(this,
                    "Error sending request: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
            if (!isConnected) {
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        ClientMainMenuGUI client = new ClientMainMenuGUI("localhost", 12345);
        if (client.connectionTimeout)
            System.exit(0);
    }
}