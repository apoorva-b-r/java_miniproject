package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.model.User;

public class TasksTab extends JPanel {

    private final User loggedInUser;
    private DefaultListModel<String> taskListModel;

    public TasksTab(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());

        JLabel title = new JLabel("✅ To-Do List", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        taskListModel = new DefaultListModel<>();
        JList<String> taskList = new JList<>(taskListModel);

        JTextField newTaskField = new JTextField();
        JButton addTaskButton = new JButton("Add Task");

        addTaskButton.addActionListener(_ -> {
            String task = newTaskField.getText().trim();
            if (!task.isEmpty()) {
                taskListModel.addElement(task);
                newTaskField.setText("");
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(newTaskField, BorderLayout.CENTER);
        inputPanel.add(addTaskButton, BorderLayout.EAST);

        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    public void reloadTasks() {
        // In the future, load from DB using TaskDAO
    }
}
