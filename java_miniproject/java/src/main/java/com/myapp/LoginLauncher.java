package com.myapp;

import javax.swing.SwingUtilities;
import com.myapp.ui.SignInForm;

public class LoginLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignInForm());
    }
}
