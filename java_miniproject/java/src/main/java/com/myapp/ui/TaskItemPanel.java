package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.dao.TaskDAO;
import com.myapp.model.Task;

public class TaskItemPanel extends JPanel {
    private final Task task;
    private final TaskDAO taskDAO = new TaskDAO();
    private final Runnable onStatusChange;
    private JLabel titleLabel;
    private JButton editButton;
    private JButton deleteButton;
    private JCheckBox doneCheckBox;

    public TaskItemPanel(Task task, Runnable onStatusChange) {
        this.task = task;
        this.onStatusChange = onStatusChange;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Left: checkbox + title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        doneCheckBox = new JCheckBox();
        doneCheckBox.setSelected(task.getStatus().equals("completed"));
        titleLabel = new JLabel(task.getTitle());
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        leftPanel.add(doneCheckBox);
        leftPanel.add(titleLabel);

        // Right: Edit + Delete buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        rightPanel.add(editButton);
        rightPanel.add(deleteButton);

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // ---- Handlers ----

        // Toggle complete/incomplete
doneCheckBox.addActionListener(_ -> {
    task.setStatus(doneCheckBox.isSelected() ? "completed" : "scheduled");
    try {
        taskDAO.updateTaskStatus(task);
        if (onStatusChange != null) onStatusChange.run(); // 🔥 Notify parent
    } catch (Exception e) {
        e.printStackTrace();
    }
});

        // Click on title -> show info
        titleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(
                    TaskItemPanel.this,
                    "Title: " + task.getTitle() + "\nDescription: " + task.getDescription(),
                    "Task Info",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        // Edit button
        editButton.addActionListener(_ -> showEditDialog());

        // Delete button
        deleteButton.addActionListener(_ -> deleteTask());
    }

    private void showEditDialog() {
        JTextField titleField = new JTextField(task.getTitle());
        JTextArea descArea = new JTextArea(task.getDescription(), 4, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(descArea);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Description:"));
        panel.add(scroll);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit Task",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            task.setTitle(titleField.getText().trim());
            task.setDescription(descArea.getText().trim());

            try {
                taskDAO.updateTask(task);
                titleLabel.setText(task.getTitle());
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating task: " + e.getMessage());
            }
        }
    }

    private void deleteTask() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                taskDAO.deleteTask(task.getId());
                Container parent = getParent();
                parent.remove(this);
                parent.revalidate();
                parent.repaint();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting task: " + e.getMessage());
            }
        }
    }
}