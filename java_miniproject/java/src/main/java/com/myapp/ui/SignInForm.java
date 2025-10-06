package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import com.myapp.model.User;
import com.myapp.dao.UserDAO;

public class SignInForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public SignInForm() {
        setTitle("Sign In");
        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        // Username Field
        txtUsername = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        // Password Field
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        // Buttons panel
        JPanel btnPanel = new JPanel();
        JButton btnSignIn = new JButton("Sign In");
        JButton btnSignUp = new JButton("Sign Up");
        btnPanel.add(btnSignIn);
        btnPanel.add(btnSignUp);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnPanel, gbc);

        add(panel);

        // Button actions
        btnSignIn.addActionListener(_ -> signInUser());
        btnSignUp.addActionListener(_ -> {
            dispose();
            new SignUpForm().setVisible(true);
        });

        setVisible(true);
    }

    private void signInUser() {
        try {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
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

    private static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
