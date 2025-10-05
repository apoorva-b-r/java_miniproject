package com.myapp.ui;

import com.myapp.model.Event;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class EventTableModel extends AbstractTableModel {
    private List<Event> events;
    private final String[] columns = {"User ID", "Title", "Description", "Start Time", "End Time", "Status"};

    public EventTableModel(List<Event> events) {
        this.events = events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        fireTableDataChanged();  // refresh table display automatically
    }


    @Override
    public int getRowCount() {
        return events.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Event e = events.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> e.getUserId();
            case 1 -> e.getTitle();
            case 2 -> e.getDescription();
            case 3 -> e.getStartTime();
            case 4 -> e.getEndTime();
            case 5 -> e.getStatus();
            default -> null;
        };
    }
}
