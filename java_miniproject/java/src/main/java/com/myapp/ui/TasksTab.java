package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.myapp.dao.TaskDAO;
import com.myapp.dao.TaskListDAO;
import com.myapp.model.Task;
import com.myapp.model.TaskList;
import com.myapp.model.User;

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

public void reloadTasks() {
    listsContainer.removeAll();
    taskLists.clear();

    try {
        List<TaskList> allLists = listDAO.getListsByUserId(loggedInUser.getId());
        List<Task> allTasks = taskDAO.getAllTasks(loggedInUser.getId());

        // Pending tasks panel
        TaskList pendingList = new TaskList();
        pendingList.setId(-1);
        pendingList.setTitle("Pending Tasks");

        List<Task> pendingTasks = allTasks.stream()
            .filter(t -> !"completed".equalsIgnoreCase(t.getStatus()))
            .toList();

        TaskListPanel pendingPanel = new TaskListPanel(pendingList);
        listsContainer.add(pendingPanel);
        listsContainer.revalidate();
        listsContainer.repaint();
        pendingPanel.loadTasks(pendingTasks);

        // Normal lists
        allLists.sort((l1, l2) -> l2.getCreatedAt().compareTo(l1.getCreatedAt()));
        for (TaskList list : allLists) {
            TaskListPanel card = new TaskListPanel(list);
            listsContainer.add(card);
            listsContainer.revalidate();
            listsContainer.repaint();

            List<Task> tasksForList = allTasks.stream()
                .filter(t -> t.getListId() == list.getId())
                .toList();

            card.loadTasks(tasksForList);
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