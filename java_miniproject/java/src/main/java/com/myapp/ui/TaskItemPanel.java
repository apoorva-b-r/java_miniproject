package com.myapp.ui;

import javax.swing.*;
import com.myapp.model.Task;
import com.myapp.dao.TaskDAO;

public class TaskItemPanel extends JPanel {
    private final JCheckBox checkBox;
    private final TaskDAO taskDAO = new TaskDAO();

    public TaskItemPanel(Task task) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        checkBox = new JCheckBox(task.getTitle(), task.getStatus().equals("completed"));
        add(checkBox);

        // Update status in DB when toggled
        checkBox.addActionListener(_ -> {
            task.setStatus(checkBox.isSelected() ? "completed" : "scheduled");
            try {
                taskDAO.updateTaskStatus(task);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Show task info on click
        checkBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(TaskItemPanel.this,
                        "Title: " + task.getTitle() + "\nDescription: " + task.getDescription(),
                        "Task Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}
