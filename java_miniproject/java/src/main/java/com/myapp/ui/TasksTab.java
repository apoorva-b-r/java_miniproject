package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.myapp.dao.TaskDAO;
import com.myapp.dao.TaskListDAO;
import com.myapp.model.Task;
import com.myapp.model.TaskList;
import com.myapp.model.User;
import com.myapp.model.Subject;

public class TasksTab extends JPanel {

    private final User loggedInUser;
    private final JPanel listsContainer;
    private final TaskDAO taskDAO = new TaskDAO();
    private final TaskListDAO listDAO = new TaskListDAO();
    private final List<TaskListPanel> taskLists = new ArrayList<>();

    public TasksTab(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel title = new JLabel("🗂️ Your To-Do Lists", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Container for all lists
        listsContainer = new JPanel(new GridLayout(0, 2, 10, 10)); // 2 columns, auto rows
        JScrollPane scrollPane = new JScrollPane(listsContainer);
        add(scrollPane, BorderLayout.CENTER);

        // Add new list section
        JPanel addListPanel = new JPanel(new BorderLayout(5, 5));
        JTextField newListField = new JTextField();
        JButton addListBtn = new JButton("➕ Add List");

        addListBtn.addActionListener(_ -> {
            String listName = newListField.getText().trim();
            if (!listName.isEmpty()) {
                addNewList(listName);
                newListField.setText("");
            }
        });

        addListPanel.add(newListField, BorderLayout.CENTER);
        addListPanel.add(addListBtn, BorderLayout.EAST);
        add(addListPanel, BorderLayout.SOUTH);

        // Initial load
        reloadTasks();
    }

    private void addNewList(String listName) {
        try {
            TaskList newList = new TaskList();
            newList.setTitle(listName);
            newList.setUserId(loggedInUser.getId());

            // Save to DB
            listDAO.createTaskList(newList);

            // Add UI card
            TaskListPanel newListPanel = new TaskListPanel(newList);
            taskLists.add(0, newListPanel);
            listsContainer.add(newListPanel, 0);
            listsContainer.revalidate();
            listsContainer.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding list: " + e.getMessage());
        }
    }

    public void addListForSubject(Subject subject) {
    try {
        TaskList newList = new TaskList();
        newList.setUserId(loggedInUser.getId());
        newList.setTitle(subject.getName());
        newList.setCreatedAt(java.time.LocalDateTime.now());

        listDAO.createTaskList(newList);

        // Instantly add to UI
        TaskListPanel listPanel = new TaskListPanel(newList);
        listsContainer.add(listPanel);
        listsContainer.revalidate();
        listsContainer.repaint();

        System.out.println("✅ Created new task list for subject: " + subject.getName());
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void reloadTasks() {
    listsContainer.removeAll();
    taskLists.clear();

    try {
        List<TaskList> allLists = listDAO.getListsByUserId(loggedInUser.getId());
        List<Task> allTasks = taskDAO.getAllTasks(loggedInUser.getId());

        // ✅ Optional: Pending Tasks section
        TaskList pendingList = new TaskList();
        pendingList.setId(-1);
        pendingList.setTitle("Pending Tasks");


        // Get current time and filter
        LocalDateTime now = LocalDateTime.now();

        List<Task> pendingTasks = allTasks.stream()
            .filter(task -> {
                // Find parent list of this task
                TaskList parentList = allLists.stream()
                        .filter(l -> l.getId() == task.getListId())
                        .findFirst()
                        .orElse(null);

                // Skip if no parent list found
                if (parentList == null) return false;

                // Skip if subject-based list
                if (parentList.getTitle().toLowerCase().contains("subject")) return false;

                // Check 24 hours rule
                LocalDateTime createdAt = parentList.getCreatedAt();
                if (createdAt == null) return false;

                long hoursSinceCreated = java.time.Duration.between(createdAt, now).toHours();

                // ✅ Include only if older than 24 hours AND not completed
                return hoursSinceCreated >= 24 &&
                    !"completed".equalsIgnoreCase(task.getStatus());
            })
            .toList();
        TaskListPanel pendingPanel = new TaskListPanel(pendingList);
        pendingPanel.loadTasks(pendingTasks);
        listsContainer.add(pendingPanel);

        // ✅ Now load each task list panel
        allLists.sort((l1, l2) -> l2.getCreatedAt().compareTo(l1.getCreatedAt()));

        for (TaskList list : allLists) {
            List<Task> tasksForList = allTasks.stream()
                .filter(t -> t.getListId() == list.getId())
                .toList();

            TaskListPanel card = new TaskListPanel(list);
            card.loadTasks(tasksForList);
            listsContainer.add(card);

            System.out.println("List: " + list.getTitle() + " -> " + tasksForList.size() + " tasks");
        }

        listsContainer.revalidate();
        listsContainer.repaint();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public void refreshTasks() {
        reloadTasks();
    }
}