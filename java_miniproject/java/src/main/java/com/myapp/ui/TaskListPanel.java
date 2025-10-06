package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.myapp.dao.TaskDAO;
import com.myapp.model.Task;
import com.myapp.model.TaskList;

public class TaskListPanel extends JPanel {
    private final TaskList taskList;
    private JPanel tasksPanel;
    private final TaskDAO taskDAO = new TaskDAO();
    private List<TaskItemPanel> tasks = new ArrayList<>();

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

        addTaskBtn.addActionListener(_ -> showAddTaskDialog());

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
        // 1️⃣ Create Task object
        Task task = new Task();
        task.setListId(taskList.getId()); // link to this list
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus("scheduled");

        // 2️⃣ Save to DB
        TaskDAO dao = new TaskDAO();
        dao.createTask(task); // make sure your DAO has this method

        // 3️⃣ Add to UI
        TaskItemPanel taskPanel = new TaskItemPanel(task);
        tasks.add(0, taskPanel); // newest first
        tasksPanel.add(taskPanel, 0);
        tasksPanel.revalidate();
        tasksPanel.repaint();

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error adding task: " + e.getMessage());
    }
}

private void showAddTaskDialog() {
    JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));

    JTextField titleField = new JTextField();
    JTextArea descArea = new JTextArea(4, 20);
    descArea.setLineWrap(true);
    descArea.setWrapStyleWord(true);
    JScrollPane descScroll = new JScrollPane(descArea);

    panel.add(new JLabel("Task Title:"));
    panel.add(titleField);
    panel.add(new JLabel("Description:"));
    panel.add(descScroll);

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Add New Task",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result == JOptionPane.OK_OPTION) {
        String title = titleField.getText().trim();
        String desc = descArea.getText().trim();

        if (!title.isEmpty()) {
            addNewTask(title, desc); // Save to DB and add to UI
        } else {
            JOptionPane.showMessageDialog(this, "Task title cannot be empty.");
        }
    }
}

}
