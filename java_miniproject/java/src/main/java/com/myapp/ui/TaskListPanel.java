package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.myapp.dao.TaskDAO;
import com.myapp.model.Task;
import com.myapp.model.TaskList;

public class TaskListPanel extends JPanel {
    private final TaskList taskList;
    private JPanel tasksPanel;
    private final TaskDAO taskDAO = new TaskDAO();

    public TaskListPanel(TaskList list) {
        this.taskList = list;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder(list.getTitle()));

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
                addNewTask(taskTitle, ""); // you can modify to add description
                taskField.setText("");
            }
        });

        inputPanel.add(taskField, BorderLayout.CENTER);
        inputPanel.add(addTaskBtn, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Load tasks from DB
        loadTasks();
    }

    private void loadTasks() {
        tasksPanel.removeAll();
        try {
            List<Task> tasks = taskDAO.getTasksByListId(taskList.getId());

            for (Task t : tasks) {
                JCheckBox cb = new JCheckBox(t.getTitle(), t.getStatus().equals("completed"));
                
                // Update status on checkbox toggle
                cb.addActionListener(_ -> {
                    t.setStatus(cb.isSelected() ? "completed" : "scheduled");
                    try {
                        taskDAO.updateTaskStatus(t);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                // Show task info on click
                cb.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        JOptionPane.showMessageDialog(TaskListPanel.this,
                                "Title: " + t.getTitle() + "\nDescription: " + t.getDescription(),
                                "Task Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                });

                tasksPanel.add(cb);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        tasksPanel.revalidate();
        tasksPanel.repaint();
    }

    public void addNewTask(String title, String description) {
        try {
            Task t = new Task();
            t.setListId(taskList.getId());
            t.setTitle(title);
            t.setDescription(description);
            t.setStatus("scheduled");

            taskDAO.createTask(t); // save to DB

            loadTasks(); // reload panel
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
