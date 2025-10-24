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
    private JPanel previewPanel;     // shows first 2-3 tasks
    private JPanel fullTasksPanel;   // all tasks
    private boolean expanded = false;
    private final TaskDAO taskDAO = new TaskDAO();
    private List<TaskItemPanel> taskPanels = new ArrayList<>();

    public TaskListPanel(TaskList list) {
    this.taskList = list;
    setLayout(new BorderLayout(5,5));

    // Header panel with title and Add button
    JPanel headerPanel = new JPanel(new BorderLayout());
    
    JLabel titleLabel = new JLabel(list.getTitle());
    titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    titleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        openFullList();
    }
    });


    // Add Task button
    JButton addTaskBtn = new JButton("+");
    addTaskBtn.setToolTipText("Add new task");
    addTaskBtn.addActionListener(_ -> showAddTaskDialog());
    headerPanel.add(addTaskBtn, BorderLayout.EAST);

    add(headerPanel, BorderLayout.NORTH);

    // Preview panel (top 2-3 tasks)
    previewPanel = new JPanel();
    previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
    add(previewPanel, BorderLayout.CENTER);

    // Full tasks panel (hidden initially)
    fullTasksPanel = new JPanel();
    fullTasksPanel.setLayout(new BoxLayout(fullTasksPanel, BoxLayout.Y_AXIS));
    fullTasksPanel.setVisible(false);
    add(fullTasksPanel, BorderLayout.SOUTH);

    // Load tasks from DB
    loadTasks();

    // Toggle expand/collapse on title click
    titleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            toggleExpanded();
        }
    });
}


    private void toggleExpanded() {
        expanded = !expanded;
        fullTasksPanel.setVisible(expanded);
        revalidate();
        repaint();
    }

    private void loadTasks() {
        previewPanel.removeAll();
        fullTasksPanel.removeAll();
        taskPanels.clear();

        try {
            List<Task> tasks = taskDAO.getTasksByListId(taskList.getId());
            for (int i = 0; i < tasks.size(); i++) {
                TaskItemPanel taskPanel = new TaskItemPanel(tasks.get(i));
                taskPanels.add(taskPanel);
                if (i < 2) previewPanel.add(taskPanel);   // preview 2 tasks
                fullTasksPanel.add(taskPanel);            // full list
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        revalidate();
        repaint();
    }

    public void loadTasks(List<Task> preloadedTasks) {
    previewPanel.removeAll();
    fullTasksPanel.removeAll();
    taskPanels.clear();

    for (int i = 0; i < preloadedTasks.size(); i++) {
        Task t = preloadedTasks.get(i);
        TaskItemPanel taskPanel = new TaskItemPanel(t);
        taskPanels.add(taskPanel);

        // first 2 tasks go to preview
        if (i < 2) previewPanel.add(taskPanel);
        fullTasksPanel.add(taskPanel); // all tasks go here
    }

    revalidate();
    repaint();
}


    // Optional: method to add new task
public void addNewTask(String title, String desc) {
    if (taskList.getId() == -1) { // pending tasks cannot add new
        JOptionPane.showMessageDialog(this, "Cannot add tasks to Pending Tasks list.");
        return;
    }

    try {
        Task task = new Task();
        task.setListId(taskList.getId());
        task.setTitle(title);
        task.setDescription(desc);
        task.setStatus("scheduled");

        taskDAO.createTask(task);

        TaskItemPanel taskPanel = new TaskItemPanel(task);
        taskPanels.add(taskPanel);

        // Add to preview if there are less than 2 tasks currently
        if (previewPanel.getComponentCount() < 2) {
            previewPanel.add(taskPanel);
        }

        fullTasksPanel.add(taskPanel);

        revalidate();
        repaint();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error adding task: " + e.getMessage());
    }
}

private void openFullList() {
    JFrame frame = new JFrame(taskList.getTitle());
    frame.setSize(400, 500);
    frame.setLocationRelativeTo(null);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    try {
        List<Task> tasks = taskDAO.getTasksByListId(taskList.getId());
        for (Task t : tasks) {
            panel.add(new TaskItemPanel(t));
        }
    } catch (Exception e) { e.printStackTrace(); }

    JScrollPane scrollPane = new JScrollPane(panel);
    frame.add(scrollPane);
    frame.setVisible(true);
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
                addNewTask(title, desc);
            } else {
                JOptionPane.showMessageDialog(this, "Task title cannot be empty.");
            }
        }
    }
}
