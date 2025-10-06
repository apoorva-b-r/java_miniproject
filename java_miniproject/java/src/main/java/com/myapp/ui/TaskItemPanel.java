package com.myapp.ui;

import javax.swing.*;
import java.awt.*;

public class TaskItemPanel extends JPanel {

    private boolean isDone = false;
    private String title;
    private String description;
    private JCheckBox checkBox;
    private JLabel titleLabel;

    public TaskItemPanel(String title, String description) {
        this.title = title;
        this.description = description;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        checkBox = new JCheckBox();
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        add(checkBox, BorderLayout.WEST);
        add(titleLabel, BorderLayout.CENTER);

        // When checkbox is clicked
        checkBox.addActionListener(_ -> {
            isDone = checkBox.isSelected();
            titleLabel.setForeground(isDone ? Color.GRAY : Color.BLACK);
            titleLabel.setText(isDone ? "<html><strike>" + title + "</strike></html>" : title);
        });

        // When clicked → show info dialog
        titleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showTaskDetails();
            }
        });
    }

    private void showTaskDetails() {
        JTextArea details = new JTextArea(description.isEmpty() ? "No description provided." : description);
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);

        JOptionPane.showMessageDialog(this,
                new JScrollPane(details),
                "📋 " + title,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
