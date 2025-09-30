package com.myapp.ui;

import com.myapp.dao.EventDAO;
import com.myapp.model.Event;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AddEventForm extends JFrame {
    private JTextField txtEventName;
    private JTextField txtDate;

    public AddEventForm() {
        setTitle("Add Event");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JLabel lblEventName = new JLabel("Event Name:");
        txtEventName = new JTextField();
        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):");
        txtDate = new JTextField();
        JButton btnSave = new JButton("Save");

        panel.add(lblEventName);
        panel.add(txtEventName);
        panel.add(lblDate);
        panel.add(txtDate);
        panel.add(new JLabel());
        panel.add(btnSave);

        add(panel);

        btnSave.addActionListener(_ -> saveEvent());

        setVisible(true);
    }

    private void saveEvent() {
        try {
            String name = txtEventName.getText();
            String dateStr = txtDate.getText();

            // Simple validation
            if (name.isBlank() || dateStr.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            // Convert to LocalDateTime
            LocalDate date = LocalDate.parse(dateStr);
            LocalDateTime startTime = date.atTime(9, 0); // default 9 AM
            LocalDateTime endTime = date.atTime(10, 0);  // default 10 AM

            // Build Event
            Event event = new Event();
            event.setUserId(1); // later: replace with logged-in user id
            event.setTitle(name);
            event.setDescription("N/A");
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setReminderBeforeMinutes(10);
            event.setStatus("scheduled");

            // Save to DB in background
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    EventDAO dao = new EventDAO();
                    dao.save(event);
                    return null;
                }

                @Override
                protected void done() {
                    JOptionPane.showMessageDialog(AddEventForm.this, "Event saved to database!");
                    dispose(); // close form
                }
            };
            worker.execute();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
