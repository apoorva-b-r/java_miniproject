package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.model.User;
import com.myapp.dao.UserDAO;
import java.security.MessageDigest;

public class SignUpForm extends JFrame {

    private JTextField txtName, txtMobile, txtGmail, txtUsername;
    private JPasswordField txtPassword, txtConfirmPassword;

    public SignUpForm() {
        setTitle("Sign Up");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        txtName = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtName, gbc);

        // Mobile
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mobile Number:"), gbc);
        txtMobile = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtMobile, gbc);

        // Gmail
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Gmail ID:"), gbc);
        txtGmail = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtGmail, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Username:"), gbc);
        txtUsername = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Password:"), gbc);
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Confirm Password:"), gbc);
        txtConfirmPassword = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtConfirmPassword, gbc);

        // Buttons panel
        JPanel btnPanel = new JPanel();
        JButton btnSignUp = new JButton("Sign Up");
        JButton btnBack = new JButton("Back to Sign In");
        btnPanel.add(btnSignUp);
        btnPanel.add(btnBack);

        gbc.gridx = 0; gbc.gridy = 6;
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
            String name = txtName.getText().trim();
            String mobile = txtMobile.getText().trim();
            String gmail = txtGmail.getText().trim();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();

            if (name.isEmpty() || mobile.isEmpty() || gmail.isEmpty() ||
                username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                return;
            }

            if (!isValidGmail(gmail)) {
                JOptionPane.showMessageDialog(this, "Gmail ID must be valid and end with @gmail.com");
            return;
            }

            if (!isValidMobile(mobile)) {
                JOptionPane.showMessageDialog(this, "Mobile number must be 10 digits.");
                return;
            }


            User user = new User();
            user.setName(name);
            user.setMobile(mobile);
            user.setGmail(gmail);
            user.setUsername(username);
            user.setPasswordHash(hashPassword(password));

            UserDAO dao = new UserDAO();
            dao.createUser(user);

            JOptionPane.showMessageDialog(this, "Sign-up successful! Your user ID: " + user.getId());
            dispose();
            new SignInForm().setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    private boolean isValidGmail(String gmail) {
    // Basic Gmail pattern check
    return gmail.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$");
    }
    private boolean isValidMobile(String mobile) {
        // Basic mobile number pattern check (10 digits)
        return mobile.matches("^\\d{10}$");
    }

    private static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
