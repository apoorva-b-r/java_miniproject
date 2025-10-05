package com.myapp.service;

import com.myapp.dao.UserDAO;
import com.myapp.model.User;
import org.mindrot.jbcrypt.BCrypt; // for password hashing

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    // Register a new user
    public boolean register(String username, String password) {
        // Hash the password securely
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hashed);
        return userDAO.createUser(user);
    }

    // Login user
    public boolean login(String username, String password) {
        // Fetch user by username
        User user = getUserByUsername(username);
        if (user == null) return false;

        // Verify password
        return BCrypt.checkpw(password, user.getPasswordHash());
    }

    // Helper method to get user by username
    private User getUserByUsername(String username) {
        for (User u : userDAO.getAllUsers()) { // simple approach
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }
}
