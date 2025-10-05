package com.myapp.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final CalendarView calendarView = new CalendarView();

    public MainFrame() {
        setTitle("Study Productivity App");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Sidebar
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        JButton calendarBtn = new JButton("📅 Calendar View");
        JButton addEventBtn = new JButton("➕ Add Event");
        sidePanel.add(calendarBtn);
        sidePanel.add(Box.createRigidArea(new Dimension(0,10)));
        sidePanel.add(addEventBtn);

        // --- Content area
        contentPanel.add(calendarView, "calendar");

        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // --- Button actions
        calendarBtn.addActionListener(_ -> cardLayout.show(contentPanel, "calendar"));
        addEventBtn.addActionListener(_ -> {
            AddEventForm form = new AddEventForm();
            form.setVisible(true);
        });

        setVisible(true);
    }
}
