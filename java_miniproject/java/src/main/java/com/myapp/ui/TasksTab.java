package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.myapp.dao.TaskDAO;
import com.myapp.db.DatabaseConnection;
import com.myapp.model.Task;
import com.myapp.model.TaskList;
import com.myapp.model.User;

public class TasksTab extends JPanel {

    private final User loggedInUser;
    private JPanel listsContainer;
    private List<TaskListPanel> taskLists = new ArrayList<>();
    private final TaskDAO taskDAO = new TaskDAO();
    private List<TaskList> allLists;
    private List<Task> allTasks;

    public TasksTab(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("🗂️ Your To-Do Lists", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Container for all lists
        listsContainer = new JPanel();
        listsContainer.setLayout(new BoxLayout(listsContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listsContainer);
        add(scrollPane, BorderLayout.CENTER);

        // Panel to add new list
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

                try {
            // 1️⃣ Load all lists for this user
            allLists = taskDAO.getListsByUser(loggedInUser.getId());

            // 2️⃣ Load all tasks across all lists
            allTasks = taskDAO.getAllTasks(loggedInUser.getId());

        } catch (Exception e) {
            e.printStackTrace();
            allLists = new ArrayList<>();
            allTasks = new ArrayList<>();
        }

        // Load initial data (in future from DB)
        reloadTasks();
    }

private void addNewList(String listName) {
    try {
        TaskList newList = new TaskList();
        newList.setTitle(listName);
        newList.setUserId(loggedInUser.getId());

        // Save to DB
        TaskDAO dao = new TaskDAO();
        dao.createTaskList(newList); // <-- make sure this method exists in TaskDAO

        // Create the panel for UI
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
        TaskDAO dao = new TaskDAO();
        // 1️⃣ Sort all lists by creation date (newest first)
        allLists.sort((l1, l2) -> l2.getCreatedAt().compareTo(l1.getCreatedAt()));

        // 2️⃣ Add Pending Tasks panel at top
        TaskList pendingList = new TaskList();
        pendingList.setId(-1);  // special id
        pendingList.setTitle("Pending Tasks");

        allTasks = taskDAO.getAllTasks(loggedInUser.getId());
        List<Task> pendingTasks = allTasks.stream()
            .filter(t -> !t.getStatus().equals("completed"))
            .toList();

        TaskListPanel pendingPanel = new TaskListPanel(pendingList);
        pendingPanel.loadTasks(pendingTasks);
        listsContainer.add(pendingPanel);

        // 3️⃣ Add normal lists
        for (TaskList list : allLists) {
        TaskListPanel panel = new TaskListPanel(list);
        listsContainer.add(panel);
        }

        listsContainer.revalidate();
        listsContainer.repaint();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}