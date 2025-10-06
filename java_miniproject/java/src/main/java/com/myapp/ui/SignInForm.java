package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.model.User;
import com.myapp.dao.UserDAO;
import java.security.MessageDigest;

public class SignInForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public SignInForm() {
        setTitle("Sign In");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel lblUsername = new JLabel("Username:");
        txtUsername = new JTextField();
        JLabel lblPassword = new JLabel("Password:");
        txtPassword = new JPasswordField();
        JButton btnSignIn = new JButton("Sign In");
        JButton btnSignup = new JButton("Sign Up");

        panel.add(lblUsername);
        panel.add(txtUsername);
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(new JLabel());
        panel.add(btnSignIn);

        add(panel);

        btnSignIn.addActionListener(_ -> signInUser());
        // --- Switch to Sign-Up Form ---
        btnSignup.addActionListener(_ -> {
            dispose();
            new SignUpForm().setVisible(true);
        });

        setVisible(true);
    }

    private void signInUser() {
        try {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            if (username.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            UserDAO dao = new UserDAO();
            User user = dao.getUserByUsername(username);

            if (user == null) {
                JOptionPane.showMessageDialog(this, "User not found!");
                return;
            }

            // Check password
            if (user.getPasswordHash().equals(hashPassword(password))) {
                JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + user.getUsername());

                // Open main frame or calendar view for this user
                new MainFrame(user).setVisible(true);
                dispose(); // close login form

            } else {
                JOptionPane.showMessageDialog(this, "Incorrect password!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    

    // Hash password same as in SignUpForm
    private static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

}
