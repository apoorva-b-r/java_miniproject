package com.myapp.ui;

import com.myapp.dao.SubjectDAO;
import com.myapp.model.Subject;
import com.myapp.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SubjectsTab extends JPanel {
    private final User loggedInUser;
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final JPanel subjectsContainer = new JPanel();

    public SubjectsTab(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("📚 Your Subjects", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Subjects grid
        subjectsContainer.setLayout(new GridLayout(0, 3, 10, 10)); // 3 columns
        JScrollPane scrollPane = new JScrollPane(subjectsContainer);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom input for new subject
        JPanel addPanel = new JPanel(new BorderLayout(5, 5));
        JTextField nameField = new JTextField();
        JButton addButton = new JButton("➕ Add Subject");

        addButton.addActionListener(_ -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Subject name cannot be empty!");
                return;
            }

            try {
                Subject subject = new Subject();
                subject.setUserId(loggedInUser.getId());
                subject.setName(name);
                subject.setColor("#D3E0EA"); // default color

                subjectDAO.createSubject(subject);
                nameField.setText("");
                reloadSubjects();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding subject: " + e.getMessage());
            }
        });

        addPanel.add(nameField, BorderLayout.CENTER);
        addPanel.add(addButton, BorderLayout.EAST);
        add(addPanel, BorderLayout.SOUTH);

        reloadSubjects();
    }

    public void reloadSubjects() {
        subjectsContainer.removeAll();

        try {
            List<Subject> subjects = subjectDAO.getSubjectsByUser(loggedInUser.getId());
            for (Subject s : subjects) {
                subjectsContainer.add(new SubjectPanel(s));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load subjects: " + e.getMessage());
        }

        subjectsContainer.revalidate();
        subjectsContainer.repaint();
    }
}