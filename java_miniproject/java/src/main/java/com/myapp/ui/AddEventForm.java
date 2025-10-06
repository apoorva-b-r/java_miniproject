package com.myapp.ui;

import com.myapp.dao.EventDAO;
import com.myapp.model.Event;
import com.myapp.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddEventForm extends JFrame {
    private JTextField txtTitle;
    private JTextArea txtDescription;
    private JTextField txtStartTime;
    private JTextField txtEndTime;
    private JTextField txtReminder;
    private JComboBox<String> cmbStatus;

    private final User loggedInUser;
    private final MainFrame mainFrame;

    public AddEventForm(MainFrame mainFrame, User loggedInUser) {
        this.mainFrame = mainFrame;
        this.loggedInUser = loggedInUser;

        setTitle("Add Event");
        setSize(450, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("Event Title:");
        txtTitle = new JTextField();

        JLabel lblDescription = new JLabel("Description:");
        txtDescription = new JTextArea(3, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(txtDescription);

        JLabel lblStart = new JLabel("Start Time (yyyy-MM-dd HH:mm):");
        txtStartTime = new JTextField();

        JLabel lblEnd = new JLabel("End Time (yyyy-MM-dd HH:mm):");
        txtEndTime = new JTextField();

        JLabel lblReminder = new JLabel("Reminder (minutes before):");
        txtReminder = new JTextField("10");

        JLabel lblStatus = new JLabel("Status:");
        cmbStatus = new JComboBox<>(new String[]{"scheduled", "completed", "cancelled"});

        JButton btnSave = new JButton("Save");

        // Add components to panel
        panel.add(lblTitle);
        panel.add(txtTitle);
        panel.add(lblDescription);
        panel.add(descriptionScroll);
        panel.add(lblStart);
        panel.add(txtStartTime);
        panel.add(lblEnd);
        panel.add(txtEndTime);
        panel.add(lblReminder);
        panel.add(txtReminder);
        panel.add(lblStatus);
        panel.add(cmbStatus);
        panel.add(new JLabel());
        panel.add(btnSave);

        add(panel);
        setVisible(true);

        btnSave.addActionListener(_ -> saveEvent());
    }

    private void saveEvent() {
        try {
            String title = txtTitle.getText().trim();
            String description = txtDescription.getText().trim();
            String startStr = txtStartTime.getText().trim();
            String endStr = txtEndTime.getText().trim();
            String reminderStr = txtReminder.getText().trim();
            String status = (String) cmbStatus.getSelectedItem();

            // Validation
            if (title.isEmpty() || startStr.isEmpty() || endStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields (Title, Start, End).");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startTime = LocalDateTime.parse(startStr, formatter);
            LocalDateTime endTime = LocalDateTime.parse(endStr, formatter);
            int reminder = Integer.parseInt(reminderStr);

            Event event = new Event();
            event.setUserId(loggedInUser.getId());
            event.setTitle(title);
            event.setDescription(description);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setReminderBeforeMinutes(reminder);
            event.setStatus(status);

            // Background thread save
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    EventDAO dao = new EventDAO();
                    dao.save(event);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        mainFrame.refreshAllViews();
                        JOptionPane.showMessageDialog(AddEventForm.this, "Event added successfully!");
                        dispose();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
