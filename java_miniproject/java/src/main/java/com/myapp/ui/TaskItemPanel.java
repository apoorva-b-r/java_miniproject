package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.model.Task;
import com.myapp.dao.TaskDAO;

public class TaskItemPanel extends JPanel {
    private final Task task;
    private final JCheckBox checkBox;
    private final JLabel titleLabel;
    private final TaskDAO taskDAO = new TaskDAO();

    public TaskItemPanel(Task task) {
        this.task = task;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // Only checkbox toggles completion
        checkBox = new JCheckBox();
        checkBox.setSelected(task.getStatus().equals("completed"));
        add(checkBox);

        // Label displays task title
        titleLabel = new JLabel(task.getTitle());
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(titleLabel);

        // Checkbox action — only updates status
        checkBox.addActionListener(_ -> {
            this.task.setStatus(checkBox.isSelected() ? "completed" : "scheduled");
            try {
                taskDAO.updateTaskStatus(task);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Click on label shows info
        titleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(TaskItemPanel.this,
                        "Title: " + task.getTitle() + "\nDescription: " + task.getDescription(),
                        "Task Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}