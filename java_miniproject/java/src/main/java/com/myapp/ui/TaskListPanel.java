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

public TaskListPanel(TaskList taskList) {
    this.taskList = taskList;
    this.taskPanels = new ArrayList<>();

    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
    setBackground(Color.WHITE);

    // ---- Header ----
// ---- Header ----
JLabel titleLabel = new JLabel(taskList.getTitle());
titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

JButton expandButton = new JButton("▼");
expandButton.addActionListener(_ -> toggleExpanded());

// ✅ Add Task button
JButton addTaskBtn = new JButton("+");
addTaskBtn.setToolTipText("Add new task");
addTaskBtn.addActionListener(_ -> showAddTaskDialog());

// Put both buttons on the right side
JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
rightButtons.setOpaque(false);
rightButtons.add(addTaskBtn);
rightButtons.add(expandButton);

JPanel headerPanel = new JPanel(new BorderLayout());
headerPanel.setBackground(new Color(245, 245, 245));
headerPanel.add(titleLabel, BorderLayout.WEST);
headerPanel.add(rightButtons, BorderLayout.EAST);

add(headerPanel, BorderLayout.NORTH);

    // ---- Task Panels ----
    previewPanel = new JPanel();
    previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
    previewPanel.setBackground(Color.WHITE);

    fullTasksPanel = new JPanel();
    fullTasksPanel.setLayout(new BoxLayout(fullTasksPanel, BoxLayout.Y_AXIS));
    fullTasksPanel.setBackground(new Color(250, 250, 250));
    fullTasksPanel.setVisible(false); // collapsed by default

    add(previewPanel, BorderLayout.CENTER);
    add(fullTasksPanel, BorderLayout.SOUTH);

    revalidate();
    repaint();
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
                TaskItemPanel taskPanel = new TaskItemPanel(tasks.get(i), () -> {
                // When status changes, refresh pending section via parent TasksTab
                Container parent = SwingUtilities.getAncestorOfClass(TasksTab.class, this);
                if (parent instanceof TasksTab tasksTab) {
                    tasksTab.reloadTasks();  // 🔄 refresh everything dynamically
                }
                });
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
    System.out.println("Loading " + preloadedTasks.size() + " tasks into list: " + taskList.getTitle());

    previewPanel.removeAll();
    fullTasksPanel.removeAll();
    taskPanels.clear();

    for (int i = 0; i < preloadedTasks.size(); i++) {
        Task t = preloadedTasks.get(i);
        System.out.println(" -> Adding " + t.getTitle());
        TaskItemPanel taskPanel = new TaskItemPanel(preloadedTasks.get(i), () -> {
        // When status changes, refresh pending section via parent TasksTab
        Container parent = SwingUtilities.getAncestorOfClass(TasksTab.class, this);
        if (parent instanceof TasksTab tasksTab) {
            tasksTab.reloadTasks();  // 🔄 refresh everything dynamically
        }
        });
        taskPanels.add(taskPanel);

        if (i < 2) previewPanel.add(taskPanel);
        fullTasksPanel.add(taskPanel);
    }

    previewPanel.revalidate();
    fullTasksPanel.revalidate();
    previewPanel.repaint();
    fullTasksPanel.repaint();
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
        task.setUserId(taskList.getUserId());
        task.setTitle(title);
        task.setDescription(desc);
        task.setStatus("scheduled");

        // Save to DB
        taskDAO.createTask(task);
        System.out.println("Saved task with title: " + title + ", listId: " + taskList.getId());

        // ✅ Reload everything from DB so UI matches actual saved data
        loadTasks();

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
    panel.add(new TaskItemPanel(t, () -> {
        Container parent = SwingUtilities.getAncestorOfClass(TasksTab.class, this);
        if (parent instanceof TasksTab tasksTab) {
            tasksTab.reloadTasks();
        }
    }));
        }
    } catch (Exception e) { e.printStackTrace(); }
    loadTasks();
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