package it.hackerinside.etk.GUI.DTOs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * A table model for displaying certificate information in a JTable or similar component.
 * This model provides a structured view of certificate data with columns for alias,
 * common name, fingerprint, and storage location.
 * 
 * <p>The model is read-only and does not support cell editing. It wraps a list of
 * {@link CertificateTableRow} objects and provides table-oriented access to their data.</p>
 *
 * @author Francesco Valentini
 */
public class CertificateTableModel extends AbstractTableModel {

    /**
     * The names of the columns displayed in the table.
     * Column order: Alias, Common Name, Fingerprint, Location.
     */
    private final String[] columnNames = { "Alias", "Common Name", "Fingerprint", "Location" };
    
    /**
     * The underlying data storage for the table rows.
     */
    private List<CertificateTableRow> rows = new ArrayList<>();

    /**
     * Replaces the current set of rows with a new collection and notifies table listeners
     * of the data change.
     *
     * @param rows the new list of certificate table rows to display;
     *             may be empty but should not be null
     * @throws NullPointerException if the rows parameter is null
     */
    public void setRows(List<CertificateTableRow> rows) {
        this.rows = rows;
        fireTableDataChanged();
    }

    /**
     * Returns the number of rows in the table model.
     *
     * @return the number of certificate rows currently displayed
     */
    @Override
    public int getRowCount() {
        return rows.size();
    }

    /**
     * Returns the number of columns in the table model.
     * This is fixed to 4 columns: Alias, Common Name, Fingerprint, and Location.
     *
     * @return always returns 4, the number of defined columns
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the name of the specified column for display in the table header.
     *
     * @param column the column index (0-based)
     * @return the column name for the specified index
     * @throws ArrayIndexOutOfBoundsException if column is not in the range [0, 3]
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    /**
     * Retrieves the complete certificate table row data for the specified row index.
     * This provides access to the full {@link CertificateTableRow} object, including
     * the original X.509 certificate.
     *
     * @param rowIndex the row index (0-based)
     * @return the CertificateTableRow at the specified index, or null if the index is invalid
     */
    public CertificateTableRow getRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < rows.size()) {
            return rows.get(rowIndex);
        }
        return null;
    }

    /**
     * Returns the value for the cell at the specified row and column index.
     *
     * @param rowIndex the row index (0-based)
     * @param columnIndex the column index (0-based)
     * @return the value at the specified cell position according to the following mapping:
     *         <ul>
     *           <li>Column 0: Keystore alias</li>
     *           <li>Column 1: Common name</li>
     *           <li>Column 2: Truncated fingerprint</li>
     *           <li>Column 3: Key location</li>
     *         </ul>
     * @throws IndexOutOfBoundsException if rowIndex or columnIndex is out of bounds
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CertificateTableRow row = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> row.keystoreAlias();
            case 1 -> row.CommonName();
            case 2 -> row.truncatedFingerprint();
            case 3 -> row.location();
            default -> null;
        };
    }

    /**
     * Indicates whether the specified cell is editable. This implementation returns
     * false for all cells, making the table model read-only.
     *
     * @param rowIndex the row index of the cell
     * @param columnIndex the column index of the cell
     * @return always returns false, indicating no cells are editable
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; 
    }
}
