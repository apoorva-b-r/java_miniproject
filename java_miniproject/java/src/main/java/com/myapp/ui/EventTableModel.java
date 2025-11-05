package com.myapp.ui;

import com.myapp.model.Event;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class EventTableModel extends AbstractTableModel {
    private List<Event> events;
    private final String[] columns = {"Title", "Description", "Start Time", "End Time", "Status"};

    public EventTableModel(List<Event> events) {
        this.events = events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        fireTableDataChanged();  // refresh table display automatically
    }


    @Override
    public int getRowCount() {
        return events == null ? 0 : events.size();
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
        if (events == null || rowIndex >= events.size()) return null;
        Event e = events.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> e.getTitle();
            case 1 -> e.getDescription();
            case 2 -> e.getStartTime();
            case 3 -> e.getEndTime();
            default -> null;
        };
    }
}