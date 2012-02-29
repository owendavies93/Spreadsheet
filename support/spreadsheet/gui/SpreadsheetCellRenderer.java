package spreadsheet.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import spreadsheet.api.CellLocation;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.api.value.Value;
import spreadsheet.api.value.ValueVisitor;

public class SpreadsheetCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private static Border focusCellHighlightBorder = UIManager
            .getBorder("Table.focusCellHighlightBorder");

    private static Color focusCellForeground = UIManager
            .getColor("Table.focusCellForeground");

    private static Color focusCellBackground = UIManager
            .getColor("Table.focusCellBackground");

    private static Color headerBackground = UIManager
            .getColor("TableHeader.background");

    private static Color tableBackground = UIManager
            .getColor("Table.background");

    private SpreadsheetInterface spreadsheet;

    protected SpreadsheetCellRenderer(SpreadsheetInterface spreadsheet,
            Font font) {
        super();
        this.spreadsheet = spreadsheet;
        this.setFont(font);

        if (focusCellHighlightBorder == null) {
            focusCellHighlightBorder = new LineBorder(Color.GRAY, 2);
        }

        if (focusCellForeground == null) {
            focusCellForeground = Color.WHITE;
        }

        if (focusCellBackground == null) {
            focusCellBackground = Color.WHITE;
        }

        final int hbg = 238;

        if (headerBackground == null) {
            headerBackground = new Color(hbg, hbg, hbg);
        }

        if (tableBackground == null) {
            tableBackground = Color.WHITE;
        }

    }

    public final Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        if (column == 0) {
            value = String.valueOf((row + 1));
            setBorder(noFocusBorder);
            setBackground(headerBackground);
            setHorizontalAlignment(JTextField.CENTER);
        } else {
            String location =
                    SpreadsheetTableModel.convertColumn(column) + (row + 1);
            CellLocation reference = new CellLocation(location);

            Value v = spreadsheet.getValue(reference);
            value = v;
            if (v != null) {
                v.visit(new ValueVisitor() {
                    @Override
                    public void visitString(String expression) {
                        setHorizontalAlignment(JTextField.LEFT);
                    }

                    @Override
                    public void visitLoop() {
                        setHorizontalAlignment(JTextField.CENTER);
                    }

                    @Override
                    public void visitInvalid(String expression) {
                        setHorizontalAlignment(JTextField.LEFT);
                    }

                    @Override
                    public void visitDouble(double value) {
                        setHorizontalAlignment(JTextField.RIGHT);
                    }
                });
            }

            if (hasFocus) {
                setBorder(focusCellHighlightBorder);
                setForeground(focusCellForeground);
                setBackground(focusCellBackground);
            } else {
                setBorder(noFocusBorder);
                setBackground(tableBackground);
            }
        }

        setValue(value);
        return this;
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
    }

    @Override
    public void invalidate() {
    }

    @Override
    public void repaint() {
    }
}
