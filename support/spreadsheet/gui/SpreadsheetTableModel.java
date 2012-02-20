package spreadsheet.gui;

import java.io.Serializable;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import spreadsheet.api.CellLocation;
import spreadsheet.api.SpreadsheetInterface;

public class SpreadsheetTableModel implements TableModel, Serializable {

    /** The unique version identifier used for serialisation */
    private static final long serialVersionUID = 1L;

    private final int numberOfRows;
    private final int numberOfColumns;
    
    /** List of listeners */
    protected EventListenerList listenerList = new EventListenerList();

    private SpreadsheetInterface spreadsheet;

    protected SpreadsheetTableModel(SpreadsheetInterface spreadsheet, int numberOfRows, int numberOfColumns) {
        this.spreadsheet = spreadsheet;
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
    }

    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return convertColumn(columnIndex);
    }

    protected static String convertColumn(int i) {
        if (i == 0) {
            return null;
        }

        i--;

        int n = getN(i);
        int m = i - maxLength(n - 1);

        StringBuilder sb = new StringBuilder();
        // converts
        for (int j = n; j > 0; --j) {
            int v = (m % (int) Math.pow(26, j)) / (int) Math.pow(26, j - 1);
            sb.append((char) ('a' + v));
        }

        return sb.toString();
    }

    private static int getN(int input) {
        for (int i = 1; i < 15; ++i)
            if (input < maxLength(i))
                return i;

        return -1;
    }

    // sum of BASE to the power 1 to input
    private static int maxLength(int input) {
        int result = 0;
        for (int i = 1; i <= input; ++i)
            result += Math.pow(26, i);

        return result;
    }

    @Override
    public int getRowCount() {
        return numberOfRows + 1;
    }

    @Override
    public int getColumnCount() {
        return numberOfColumns;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount() || columnIndex < 0
                || columnIndex >= getColumnCount()) {
            return null;
        }

        String colName = getColumnName(columnIndex);
        if (colName != null) {
            String location = colName + (rowIndex + 1);
            CellLocation cellReference =  new CellLocation(location);
            return spreadsheet.getValue(cellReference);
        }

        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String location = getColumnName(columnIndex) + (rowIndex + 1);
        String value = ((String) aValue).trim();

        // if the expression was a number, prefix it with '=' to make it
        // a formula
        if (Util.isFormula(value) && value.charAt(0) != '=') {
            value = "=" + value;
        }

        CellLocation reference = new CellLocation(location);
        spreadsheet.setExpression(reference, value);
    }

    /**
     * Notifies all listeners that all cell values in the table's rows may have
     * changed. The number of rows may also have changed and the
     * <code>JTable</code> should redraw the table from scratch. The structure
     * of the table (as in the order of the columns) is assumed to be the same.
     * 
     * @see TableModelEvent
     * @see EventListenerList
     * @see javax.swing.JTable#tableChanged(TableModelEvent)
     */
    public void fireTableDataChanged() {
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Forwards the given notification event to all
     * <code>TableModelListeners</code> that registered themselves as listeners
     * for this table model.
     * 
     * @param e
     *            the event to be forwarded
     * 
     * @see #addTableModelListener
     * @see TableModelEvent
     * @see EventListenerList
     */
    protected void fireTableChanged(TableModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TableModelListener.class) {
                ((TableModelListener) listeners[i + 1]).tableChanged(e);
            }
        }
    }

    public void removeTableModelListener(TableModelListener l) {
        listenerList.remove(TableModelListener.class, l);
    }

    public void addTableModelListener(TableModelListener l) {
        listenerList.add(TableModelListener.class, l);
    }

}
