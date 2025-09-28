package com.myapp.ui;

import javax.swing.*;
import java.awt.*;

public class AddEventForm extends JFrame {

    public AddEventForm() {
        setTitle("Add Event");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JLabel lblEventName = new JLabel("Event Name:");
        JTextField txtEventName = new JTextField();
        JLabel lblDate = new JLabel("Date:");
        JTextField txtDate = new JTextField();
        JButton btnSave = new JButton("Save");

        panel.add(lblEventName);
        panel.add(txtEventName);
        panel.add(lblDate);
        panel.add(txtDate);
        panel.add(new JLabel());
        panel.add(btnSave);

        add(panel);

        btnSave.addActionListener(_ -> JOptionPane.showMessageDialog(this, "Event saved!"));

        setVisible(true);
    }
}
