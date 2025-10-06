package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.model.User;
import com.myapp.dao.UserDAO;
import java.security.MessageDigest;

public class SignUpForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public SignUpForm() {
        setTitle("Sign Up");
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
        JButton btnSignUp = new JButton("Sign Up");
        JButton btnBack = new JButton("Back to Sign In");
        btnPanel.add(btnSignUp);
        btnPanel.add(btnBack);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnPanel, gbc);

        add(panel);

        // Button actions
        btnSignUp.addActionListener(_ -> signUpUser());
        btnBack.addActionListener(_ -> {
            dispose();
            new SignInForm().setVisible(true);
        });

        setVisible(true);
    }

    private void signUpUser() {
        try {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(hashPassword(password));

            UserDAO dao = new UserDAO();
            dao.createUser(user); // Make sure UserDAO has this method

            JOptionPane.showMessageDialog(this, "Sign-up successful! Your user ID: " + user.getId());
            dispose();
            new SignInForm().setVisible(true);

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
