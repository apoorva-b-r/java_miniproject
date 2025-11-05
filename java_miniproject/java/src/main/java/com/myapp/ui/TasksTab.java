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
    private final JPanel subjectListsPanel;
    private final JPanel dailyListsPanel;
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

        // Main container for both sections
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        // Daily Lists Section
        JLabel dailySectionTitle = new JLabel("🗓️ Daily Task Lists");
        dailySectionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        dailySectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        dailySectionTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        mainContainer.add(dailySectionTitle);
        
        dailyListsPanel = new JPanel();
        dailyListsPanel.setLayout(new BoxLayout(dailyListsPanel, BoxLayout.Y_AXIS));
        dailyListsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContainer.add(dailyListsPanel);

        // Spacing between sections
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Subject Lists Section
        JLabel subjectSectionTitle = new JLabel("📘 Subject Task Lists");
        subjectSectionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        subjectSectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subjectSectionTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        mainContainer.add(subjectSectionTitle);
        
        subjectListsPanel = new JPanel();
        subjectListsPanel.setLayout(new BoxLayout(subjectListsPanel, BoxLayout.Y_AXIS));
        subjectListsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContainer.add(subjectListsPanel);

        // Scroll pane for main container
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Add new list section (only for daily lists)
        JPanel addListPanel = new JPanel(new BorderLayout(5, 5));
        addListPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JTextField newListField = new JTextField();
        JButton addListBtn = new JButton("➕ Add Daily List");

        addListBtn.addActionListener(_ -> {
            String listName = newListField.getText().trim();
            if (!listName.isEmpty()) {
                addNewList(listName);
                newListField.setText("");
            }
        });

        addListPanel.add(new JLabel("Daily List:"), BorderLayout.WEST);
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

            // Add UI card to daily lists
            TaskListPanel newListPanel = new TaskListPanel(newList);
            taskLists.add(0, newListPanel);
            dailyListsPanel.add(newListPanel, 0);
            dailyListsPanel.revalidate();
            dailyListsPanel.repaint();
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
            newList.setSubjectId(subject.getId());  // ✅ link to subject
            newList.setCreatedAt(java.time.LocalDateTime.now());

            listDAO.createTaskList(newList);

            // Add to subject lists section
            TaskListPanel listPanel = new TaskListPanel(newList);
            subjectListsPanel.add(listPanel);
            subjectListsPanel.revalidate();
            subjectListsPanel.repaint();

            System.out.println("✅ Created new task list for subject: " + subject.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadTasks() {
        subjectListsPanel.removeAll();
        dailyListsPanel.removeAll();
        taskLists.clear();

        try {
            List<TaskList> allLists = listDAO.getListsByUser(loggedInUser.getId());
            List<Task> allTasks = taskDAO.getAllTasks(loggedInUser.getId());

            // ✅ Optional: Pending Tasks section (add to daily lists)
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
                    if (isSubjectList(parentList, allLists)) return false;

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
            dailyListsPanel.add(pendingPanel);

            // ✅ Sort and classify lists
            allLists.sort((l1, l2) -> l2.getCreatedAt().compareTo(l1.getCreatedAt()));

            for (TaskList list : allLists) {
                List<Task> tasksForList = allTasks.stream()
                    .filter(t -> t.getListId() == list.getId())
                    .toList();

                TaskListPanel card = new TaskListPanel(list);
                card.loadTasks(tasksForList);

                // Classify: subject vs daily
                if (isSubjectList(list, allLists)) {
                    subjectListsPanel.add(card);
                } else {
                    dailyListsPanel.add(card);
                }

                System.out.println("List: " + list.getTitle() + " -> " + tasksForList.size() + " tasks");
            }

            subjectListsPanel.revalidate();
            subjectListsPanel.repaint();
            dailyListsPanel.revalidate();
            dailyListsPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines if a TaskList is subject-related based on:
     * - Title matching "subject" keyword
     * - Subject reference (if available in your model)
     * You can enhance this logic based on your actual data model
     */
private boolean isSubjectList(TaskList list, List<TaskList> allLists) {
    return list.getSubjectId() != null && list.getSubjectId() > 0;
}

    public void refreshTasks() {
        reloadTasks();
    }
}