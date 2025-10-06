package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.model.User;
import com.myapp.dao.UserDAO;

public class SignUpForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public SignUpForm() {
        setTitle("Sign Up");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel lblUsername = new JLabel("Username:");
        txtUsername = new JTextField();
        JLabel lblPassword = new JLabel("Password:");
        txtPassword = new JPasswordField();
        JButton btnSignUp = new JButton("Sign Up");
        JButton btnBack = new JButton("Back to Sign In");

        panel.add(lblUsername);
        panel.add(txtUsername);
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(new JLabel());
        panel.add(btnSignUp);
        panel.add(btnBack);

        add(panel);

        btnSignUp.addActionListener(_ -> signUpUser());
        btnBack.addActionListener(_ -> {
            dispose();
            new SignInForm().setVisible(true);
        });

        setVisible(true);
    }

    private void signUpUser() {
        try {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            if (username.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(hashPassword(password));

            UserDAO dao = new UserDAO();
            dao.createUser(user); // Make sure UserDAO has this method

            JOptionPane.showMessageDialog(this, "Sign-up successful! Your user ID: " + user.getId());
            dispose(); // close the form
            new SignInForm().setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private static String hashPassword(String password) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}

