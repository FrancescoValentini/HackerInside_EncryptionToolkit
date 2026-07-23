package it.hackerinside.etk.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Manages the visibility of columns in a {@link JTable}.
 * <p>
 * This class keeps track of all available columns and allows individual columns
 * to be shown or hidden without losing their original {@link TableColumn}
 * instances.
 */
public class ColumnVisibilityManager {

    private final JTable table;
    private final Map<CertificateColumn, TableColumn> allColumns = new EnumMap<>(CertificateColumn.class);

    /**
     * Creates a new column visibility manager for the given table.
     * <p>
     * All columns currently present in the table are stored and then hidden.
     * Columns can later be made visible again using the provided methods.
     *
     * @param table the table whose columns will be managed
     */
    public ColumnVisibilityManager(JTable table) {
        this.table = table;

        TableColumnModel model = table.getColumnModel();
        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn column = model.getColumn(i);
            allColumns.put(CertificateColumn.values()[i], column);
        }
    }


    /**
     * Changes the visibility of a single column.
     *
     * @param column the column identifier
     * @param visible {@code true} to show the column, {@code false} to hide it
     */
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

    public void hideAll() {
        for (CertificateColumn column : CertificateColumn.values()) {
            setColumnVisible(column, false);
        }
    }
    /**
     * Changes the visibility of a column using its enum name.
     * <p>
     * The name is trimmed and converted to upper case before being resolved.
     * Invalid column names are ignored.
     *
     * @param columnName the column name
     * @param visible {@code true} to show the column, {@code false} to hide it
     */
    public void setColumnVisible(String columnName, boolean visible) {
        if (columnName == null || columnName.isBlank()) {
            return;
        }

        try {
            CertificateColumn column = CertificateColumn.valueOf(columnName.trim().toUpperCase());
            setColumnVisible(column, visible);
        } catch (IllegalArgumentException e) {
            // Ignore unknown column names
        }
    }

    /**
     * Shows the columns specified by a comma-separated list of names.
     *
     * @param columnNames comma-separated column names
     */
    public void showColumns(String columnNames) {
        setColumnsVisible(columnNames, true);
    }

    /**
     * Hides the columns specified by a comma-separated list of names.
     *
     * @param columnNames comma-separated column names
     */
    public void hideColumns(String columnNames) {
        setColumnsVisible(columnNames, false);
    }

    /**
     * Changes the visibility of multiple columns specified by a comma-separated list.
     *
     * @param columnNames comma-separated column names
     * @param visible {@code true} to show columns, {@code false} to hide columns
     */
    private void setColumnsVisible(String columnNames, boolean visible) {
        if (columnNames == null || columnNames.isBlank()) {
            return;
        }

        Arrays.stream(columnNames.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(columnName -> setColumnVisible(columnName, visible));
    }


    /**
     * Shows the specified columns.
     *
     * @param columns columns to display
     */
    public void showColumns(CertificateColumn[] columns) {
        if (columns == null) {
            return;
        }

        for (CertificateColumn column : columns) {
            setColumnVisible(column, true);
        }
    }

    /**
     * Hides the specified columns.
     *
     * @param columns columns to hide
     */
    public void hideColumns(CertificateColumn[] columns) {
        if (columns == null) {
            return;
        }

        for (CertificateColumn column : columns) {
            setColumnVisible(column, false);
        }
    }

    /**
     * Returns all currently visible columns.
     *
     * @return an array containing the visible column identifiers
     */
    public CertificateColumn[] getVisibleColumns() {
        TableColumnModel model = table.getColumnModel();
        List<CertificateColumn> visibleColumns = new ArrayList<>();

        for (CertificateColumn column : CertificateColumn.values()) {
            TableColumn tableColumn = allColumns.get(column);

            for (int i = 0; i < model.getColumnCount(); i++) {
                if (model.getColumn(i) == tableColumn) {
                    visibleColumns.add(column);
                    break;
                }
            }
        }

        return visibleColumns.toArray(new CertificateColumn[0]);
    }

    /**
     * Returns the names of all currently visible columns.
     * <p>
     * Column names are returned as enum names separated by commas.
     *
     * @return comma-separated list of visible column names
     */
    public String getVisibleColumnNames() {
        return Arrays.stream(getVisibleColumns())
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }
}
