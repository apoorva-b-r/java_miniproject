package com.myapp.ui;

import com.myapp.dao.EventDAO;
import com.myapp.model.Event;
import com.myapp.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddEventForm extends JFrame {
    private JTextField txtTitle;
    private JTextArea txtDescription;
    private JTextField txtStartTime;
    private JTextField txtEndTime;
    private JTextField txtReminder;

    private final User loggedInUser;
    private final MainFrame mainFrame;
    private Event eventBeingEdited;
public AddEventForm(MainFrame mainFrame, User loggedInUser) {
        this(mainFrame, loggedInUser, null);
    }

    // Constructor for editing existing event
    public AddEventForm(MainFrame mainFrame, User loggedInUser, Event eventToEdit) {
        this.mainFrame = mainFrame;
        this.loggedInUser = loggedInUser;
        this.eventBeingEdited = eventToEdit;

        setTitle(eventToEdit == null ? "Add Event" : "Edit Event");
        setSize(450, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        if (eventToEdit != null) populateFields(eventToEdit);
        setVisible(true);
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
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
        txtReminder = new JTextField("24");

        JButton btnSave = new JButton("Save");

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
        panel.add(new JLabel());
        panel.add(btnSave);

        add(panel);

        btnSave.addActionListener(_ -> saveEvent());
    }

    private void populateFields(Event e) {
        txtTitle.setText(e.getTitle());
        txtDescription.setText(e.getDescription());
        txtStartTime.setText(e.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        txtEndTime.setText(e.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        txtReminder.setText(String.valueOf(e.getReminderBeforeMinutes()));
    }

    private void saveEvent() {
        String title = txtTitle.getText().trim();
        String description = txtDescription.getText().trim();
        String startStr = txtStartTime.getText().trim();
        String endStr = txtEndTime.getText().trim();
        String reminderStr = txtReminder.getText().trim();

        if (title.isEmpty() || startStr.isEmpty() || endStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all required fields (Title, Start, End).",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime, endTime;
        int reminder = 24;

        try {
            startTime = LocalDateTime.parse(startStr, formatter);
            endTime = LocalDateTime.parse(endStr, formatter);

            if (!endTime.isAfter(startTime)) {
                JOptionPane.showMessageDialog(this,
                        "End time must be after start time.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!reminderStr.isEmpty()) {
                reminder = Integer.parseInt(reminderStr);
                if (reminder < 0) throw new NumberFormatException();
            }

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date/time format. Use yyyy-MM-dd HH:mm",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Reminder must be a non-negative number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Event event = (eventBeingEdited != null) ? eventBeingEdited : new Event();
        event.setUserId(loggedInUser.getId());
        event.setTitle(title);
        event.setDescription(description);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setReminderBeforeMinutes(reminder);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                EventDAO dao = new EventDAO();
                if (eventBeingEdited != null) {
                    dao.update(event); // call update for existing
                } else {
                    dao.save(event);   // call save for new
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    mainFrame.refreshAllViews();
                    JOptionPane.showMessageDialog(AddEventForm.this,
                            eventBeingEdited != null ? "Event updated successfully!" : "Event added successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(AddEventForm.this,
                            "Error saving event: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}