package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.model.Subject;

public class SubjectPanel extends JPanel {
    private final Subject subject;

    public SubjectPanel(Subject subject) {
        this.subject = subject;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(200, 100));

        JLabel nameLabel = new JLabel(subject.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.DARK_GRAY);

        if (subject.getColor() != null && !subject.getColor().isEmpty()) {
            try {
                setBackground(Color.decode(subject.getColor()));
            } catch (Exception ignored) {}
        }

        add(nameLabel, BorderLayout.CENTER);

        // Open subject details or filter view on click
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(null, 
                    "Subject: " + subject.getName(),
                    "Subject Info",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    public Subject getSubject() { return subject; }
}
