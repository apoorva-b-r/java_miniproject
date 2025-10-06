package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TaskListPanel extends JPanel {

    private String listTitle;
    private JPanel tasksPanel;
    private List<TaskItemPanel> tasks = new ArrayList<>();

    public TaskListPanel(String listTitle) {
        this.listTitle = listTitle;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder(listTitle));

        // Panel for tasks
        tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(tasksPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Input field for new tasks
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JTextField taskField = new JTextField();
        JButton addTaskBtn = new JButton("Add Task");

        addTaskBtn.addActionListener(_ -> {
            String taskTitle = taskField.getText().trim();
            if (!taskTitle.isEmpty()) {
                addNewTask(taskTitle, ""); // empty desc for now
                taskField.setText("");
            }
        });

        inputPanel.add(taskField, BorderLayout.CENTER);
        inputPanel.add(addTaskBtn, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
    }

    public void addNewTask(String title, String description) {
        TaskItemPanel task = new TaskItemPanel(title, description);
        tasks.add(0, task); // newest first
        tasksPanel.add(task, 0);
        tasksPanel.revalidate();
        tasksPanel.repaint();
    }
}
