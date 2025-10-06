package com.myapp.ui;

import com.myapp.dao.EventDAO;
import com.myapp.model.Event;
import com.myapp.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AddEventForm extends JFrame {

    private JTextField txtEventName;
    private JTextField txtDescription;
    private JTextField txtStartDate;
    private JTextField txtStartTime;
    private JTextField txtEndDate;
    private JTextField txtEndTime;
    private JTextField txtReminder;
    private JComboBox<String> cmbStatus;

    private final CalendarView calendarView;
    private final User loggedInUser;

    public AddEventForm(CalendarView calendarView, User loggedInUser) {
        this.calendarView = calendarView;
        this.loggedInUser = loggedInUser;

        setTitle("Add Event");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 5));

        panel.add(new JLabel("Event Name:"));
        txtEventName = new JTextField();
        panel.add(txtEventName);

        panel.add(new JLabel("Description:"));
        txtDescription = new JTextField();
        panel.add(txtDescription);

        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        txtStartDate = new JTextField();
        panel.add(txtStartDate);

        panel.add(new JLabel("Start Time (HH:MM, 24h):"));
        txtStartTime = new JTextField("09:00"); // default
        panel.add(txtStartTime);

        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        txtEndDate = new JTextField();
        panel.add(txtEndDate);

        panel.add(new JLabel("End Time (HH:MM, 24h):"));
        txtEndTime = new JTextField("10:00"); // default
        panel.add(txtEndTime);

        panel.add(new JLabel("Reminder (minutes before):"));
        txtReminder = new JTextField("10"); // default
        panel.add(txtReminder);

        panel.add(new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"scheduled", "completed", "cancelled"});
        panel.add(cmbStatus);

        JButton btnSave = new JButton("Save");
        panel.add(new JLabel());
        panel.add(btnSave);

        add(panel);

        btnSave.addActionListener(_ -> saveEvent());

        setVisible(true);
    }

    private void saveEvent() {
        if (txtEventName.getText().isBlank() || txtStartDate.getText().isBlank() || txtStartTime.getText().isBlank() ||
            txtEndDate.getText().isBlank() || txtEndTime.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                Event event = new Event();
                event.setUserId(loggedInUser.getId());
                event.setTitle(txtEventName.getText().trim());
                event.setDescription(txtDescription.getText().trim());

                LocalDate startDate = LocalDate.parse(txtStartDate.getText().trim());
                LocalTime startTime = LocalTime.parse(txtStartTime.getText().trim());
                event.setStartTime(LocalDateTime.of(startDate, startTime));

                LocalDate endDate = LocalDate.parse(txtEndDate.getText().trim());
                LocalTime endTime = LocalTime.parse(txtEndTime.getText().trim());
                event.setEndTime(LocalDateTime.of(endDate, endTime));

                event.setReminderBeforeMinutes(Integer.parseInt(txtReminder.getText().trim()));
                event.setStatus((String) cmbStatus.getSelectedItem());

                EventDAO dao = new EventDAO();
                dao.save(event);

                calendarView.loadEventsFromDatabase(); // ✅ no-arg method
                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(AddEventForm.this, "Event saved successfully!");
                dispose();
            }
        };

        worker.execute();
    }
}
