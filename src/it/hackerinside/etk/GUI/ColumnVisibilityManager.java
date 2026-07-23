package it.hackerinside.etk.GUI;

import java.util.EnumMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class ColumnVisibilityManager {

    private final JTable table;
    private final Map<CertificateColumn, TableColumn> allColumns = new EnumMap<>(CertificateColumn.class);

    public ColumnVisibilityManager(JTable table) {
        this.table = table;

        TableColumnModel model = table.getColumnModel();
        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn column = model.getColumn(i);
            allColumns.put(CertificateColumn.values()[i], column);
        }
    }

    public void setColumnVisible(CertificateColumn column, boolean visible) {
        TableColumnModel model = table.getColumnModel();
        TableColumn tableColumn = allColumns.get(column);

        boolean currentlyVisible = false;
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (model.getColumn(i) == tableColumn) {
                currentlyVisible = true;
                break;
            }
        }

        if (visible && !currentlyVisible) {
            model.addColumn(tableColumn);
        } else if (!visible && currentlyVisible) {
            model.removeColumn(tableColumn);
        }
    }
}