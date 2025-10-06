package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.myapp.model.User;

public class TasksTab extends JPanel {

    private final User loggedInUser;
    private JPanel listsContainer;
    private List<TaskListPanel> taskLists = new ArrayList<>();

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

        // Load initial data (in future from DB)
        reloadTasks();
    }

    private void addNewList(String listName) {
        TaskListPanel newList = new TaskListPanel(listName);
        taskLists.add(0, newList); // newest first
        listsContainer.add(newList, 0);
        listsContainer.revalidate();
        listsContainer.repaint();
    }

    public void reloadTasks() {
        // In future, fetch user’s lists from DB
        listsContainer.removeAll();
        taskLists.clear();

        // Example lists to show structure
        addNewList("🧠 Study Tasks");
        addNewList("🏫 College To-Do");

        listsContainer.revalidate();
        listsContainer.repaint();
    }
}