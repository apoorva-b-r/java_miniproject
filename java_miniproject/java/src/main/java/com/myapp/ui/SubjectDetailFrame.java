package com.myapp.ui;

import javax.swing.*;
import java.awt.*;
import com.myapp.model.Subject;

public class SubjectDetailFrame extends JFrame {

    public SubjectDetailFrame(Subject subject) {
        setTitle("Subject: " + subject.getName());
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel();
        header.setBackground(Color.decode(subject.getColor()));
        JLabel nameLabel = new JLabel(subject.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(getContrastColor(Color.decode(subject.getColor())));
        header.add(nameLabel);

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        infoArea.setText("Subject Name: " + subject.getName() + "\n\n"
                        + "Color: " + subject.getColor() + "\n\n"
                        + "Here you can later show related tasks, events, or notes.");

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(infoArea), BorderLayout.CENTER);

        setVisible(true);
    }

    private Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }
}
