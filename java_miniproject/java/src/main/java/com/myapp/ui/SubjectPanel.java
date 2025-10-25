package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.model.Subject;
import com.myapp.dao.SubjectDAO;

public class SubjectPanel extends JPanel {
    private final Subject subject;
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final SubjectsTab parentTab; // 👈 reference to parent

    public SubjectPanel(Subject subject, SubjectsTab parentTab) {
        this.subject = subject;
        this.parentTab = parentTab;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(200, 100));

        JLabel nameLabel = new JLabel(subject.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // pastel background if color set
        if (subject.getColor() != null && !subject.getColor().isEmpty()) {
            try {
                setBackground(Color.decode(subject.getColor()));
            } catch (Exception ignored) {}
        }

        add(nameLabel, BorderLayout.CENTER);

        // 🖱️ Click to open edit dialog
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openEditDialog();
            }
        });
    }

    private void openEditDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Subject", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Editable fields
        JTextField nameField = new JTextField(subject.getName());
        JTextArea syllabusArea = new JTextArea(subject.getSyllabus() != null ? subject.getSyllabus() : "");
        syllabusArea.setLineWrap(true);
        syllabusArea.setWrapStyleWord(true);
        JScrollPane syllabusScroll = new JScrollPane(syllabusArea);

        // layout for input
        JPanel formPanel = new JPanel(new BorderLayout(5, 5));
        formPanel.add(new JLabel("Subject Title:"), BorderLayout.NORTH);
        formPanel.add(nameField, BorderLayout.CENTER);

        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.add(new JLabel("Syllabus / Description:"), BorderLayout.NORTH);
        descPanel.add(syllabusScroll, BorderLayout.CENTER);

        JButton saveButton = new JButton("💾 Save Changes");
        saveButton.setBackground(new Color(200, 230, 200));

        saveButton.addActionListener(_ -> {
            try {
                String newName = nameField.getText().trim();
                String newSyllabus = syllabusArea.getText().trim();

                if (newName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Subject name cannot be empty!");
                    return;
                }

                subject.setName(newName);
                subject.setSyllabus(newSyllabus);
                subjectDAO.updateSubject(subject);

                JOptionPane.showMessageDialog(dialog, "✅ Subject updated successfully!");
                dialog.dispose();

                // 🔁 Refresh subjects list in parent tab
                if (parentTab != null) {
                    parentTab.reloadSubjects();
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error updating subject: " + e.getMessage());
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.NORTH);
        dialog.add(descPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public Subject getSubject() { return subject; }
}