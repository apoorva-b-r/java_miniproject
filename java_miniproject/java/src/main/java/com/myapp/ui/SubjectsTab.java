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
    private final TasksTab tasksTab;

    public SubjectsTab(User user, TasksTab tasksTab) {
        this.loggedInUser = user;
        this.tasksTab = tasksTab;
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("📚 Your Subjects", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Container for subject cards
        subjectsContainer.setLayout(new GridLayout(0, 3, 10, 10));
        JScrollPane scrollPane = new JScrollPane(subjectsContainer);
        add(scrollPane, BorderLayout.CENTER);

        // Add new subject area
        JPanel addPanel = new JPanel(new BorderLayout(5, 5));

        // Left part - inputs
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JTextField nameField = new JTextField();
        nameField.setBorder(BorderFactory.createTitledBorder("Subject Name"));

        JTextArea syllabusArea = new JTextArea(3, 20);
        syllabusArea.setBorder(BorderFactory.createTitledBorder("Syllabus (optional)"));
        syllabusArea.setLineWrap(true);
        syllabusArea.setWrapStyleWord(true);

        inputPanel.add(nameField);
        inputPanel.add(new JScrollPane(syllabusArea));

        // Right part - color & add buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton colorButton = new JButton("🎨 Pick Color");
        JButton addButton = new JButton("➕ Add Subject");

        final Color[] selectedColor = {new Color(224, 236, 240)}; // pastel blue default

        // Color picker
        colorButton.addActionListener(_ -> {
            Color chosen = JColorChooser.showDialog(this, "Choose Subject Color", selectedColor[0]);
            if (chosen != null) {
                selectedColor[0] = chosen;
                colorButton.setBackground(chosen);
                colorButton.setForeground(getContrastColor(chosen));
            }
        });

        // Add subject button
        addButton.addActionListener(_ -> {
            String name = nameField.getText().trim();
            String syllabus = syllabusArea.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Subject name cannot be empty!");
                return;
            }

            try {
                Subject subject = new Subject();
                subject.setUserId(loggedInUser.getId());
                subject.setName(name);
                subject.setColor(toHex(selectedColor[0]));
                subject.setSyllabus(syllabus.isEmpty() ? null : syllabus);

                subjectDAO.createSubject(subject);
                nameField.setText("");
                syllabusArea.setText("");
                colorButton.setBackground(null);
                reloadSubjects();
                if (tasksTab != null) {
                tasksTab.addListForSubject(subject);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding subject: " + e.getMessage());
            }
        });

        rightPanel.add(colorButton);
        rightPanel.add(addButton);

        addPanel.add(inputPanel, BorderLayout.CENTER);
        addPanel.add(rightPanel, BorderLayout.EAST);
        add(addPanel, BorderLayout.SOUTH);

        reloadSubjects();
    }

    // Converts Color to hex string (#RRGGBB)
    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    // Contrast helper for text on colored buttons
    private Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    public void reloadSubjects() {
        subjectsContainer.removeAll();
        try {
            List<Subject> subjects = subjectDAO.getSubjectsByUser(loggedInUser.getId());
            for (Subject s : subjects) {
                subjectsContainer.add(new SubjectPanel(s, this));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load subjects: " + e.getMessage());
        }

        subjectsContainer.revalidate();
        subjectsContainer.repaint();
    }
}