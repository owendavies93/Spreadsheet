package spreadsheet.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableColumn;

import spreadsheet.api.CellLocation;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.parser.ParseException;

/**
 * This is the graphical user interface class for the Spreadsheet.
 */
public final class SpreadsheetGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Dimension FRAME_SIZE = new Dimension(640, 480);

    private static final int FIRST_COLUMN_WIDTH = 35;

    private JTable table;

    private JCheckBox autoComputeCheckBox;

    private SpreadsheetTableModel tableModel;

    private SpreadsheetInterface spreadsheet;

    public SpreadsheetGUI(SpreadsheetInterface spreadsheet, int numberOfRows,
            int numberOfColumns) {
        super("Spreadsheet");

        this.spreadsheet = spreadsheet;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // get content pane
        setLayout(new BorderLayout());

        JToolBar bar = new JToolBar("Input Expression");
        add(bar, BorderLayout.NORTH);

        bar.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel("Expression: ");
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        bar.add(label, c);

        final JTextField expressionInput = new JTextField();

        c.gridx = 1;
        c.weightx = 0.5;
        bar.add(expressionInput, c);

        JButton expressionButton = new JButton("OK");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.weightx = 0;
        bar.add(expressionButton, c);

        bar.addSeparator();

        autoComputeCheckBox = new JCheckBox();
        autoComputeCheckBox
                .setToolTipText("Automatically Re-compute Spreadsheet");
        bar.add(autoComputeCheckBox);

        final JButton computeButton = new JButton("Compute");
        computeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SpreadsheetGUI.this.recompute();
            }
        });
        bar.add(computeButton);

        // the auto-compute check box listener
        autoComputeCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBox jcb = (JCheckBox) e.getSource();
                computeButton.setEnabled(!jcb.isSelected());
            }
        });

        autoComputeCheckBox.setSelected(true);
        computeButton.setEnabled(!autoComputeCheckBox.isSelected());

        // the textbox listener
        ActionListener textListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String text = expressionInput.getText();
                try {
                    List<String[]> ops = Util.extractOperations(text);
                    for (String[] cmd : ops) {
                        CellLocation ref = new CellLocation(cmd[0]);
                        SpreadsheetGUI.this.spreadsheet.setExpression(ref,
                                cmd[1]);
                    }
                    expressionInput.setText("");
                    if (autoComputeCheckBox.isSelected()) {
                        SpreadsheetGUI.this.recompute();
                    }
                    // computed one cell only - redraw it
                    tableModel.fireTableDataChanged();
                } catch (ParseException e) {
                    // error occurred
                    JOptionPane.showMessageDialog(SpreadsheetGUI.this,
                            "Invalid Command Sequence Entered!", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    expressionInput.selectAll();
                    e.printStackTrace();
                }
            }
        };
        expressionInput.addActionListener(textListener);
        expressionButton.addActionListener(textListener);

        // create the table
        tableModel =
                new SpreadsheetTableModel(spreadsheet, numberOfRows,
                        numberOfColumns);
        table = new JTable(tableModel);
        table.setAutoCreateColumnsFromModel(false);

        SpreadsheetCellEditor sce =
                new SpreadsheetCellEditor(new JTextField(), spreadsheet);

        sce.addCellEditorListener(new CellEditorListener() {
            public void editingCanceled(javax.swing.event.ChangeEvent e) {
            };

            public void editingStopped(javax.swing.event.ChangeEvent e) {
                if (autoComputeCheckBox.isSelected()) {
                    SpreadsheetGUI.this.recompute();
                }
                tableModel.fireTableDataChanged();
            };
        });

        table.setDefaultEditor(Object.class, sce);
        table.setDefaultRenderer(Object.class, new SpreadsheetCellRenderer(
                spreadsheet, table.getFont()));

        TableColumn firstColumn = table.getColumnModel().getColumn(0);
        firstColumn.setMinWidth(FIRST_COLUMN_WIDTH);
        firstColumn.setPreferredWidth(FIRST_COLUMN_WIDTH);

        // set location and initial size
        table.setPreferredScrollableViewportSize(FRAME_SIZE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - FRAME_SIZE.width) / 2,
                (screenSize.height - FRAME_SIZE.height) / 2);

        // other preferences
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);

        // set up the scrollbars
        // setContentPane(new JScrollPane(table));
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void start() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pack();
                setVisible(true);
            }
        });
    }

    private void recompute() {
        try {
            spreadsheet.recompute();
            tableModel.fireTableDataChanged();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(SpreadsheetGUI.this,
                    "Invalid Command Encountered!", "Input Error",
                    JOptionPane.ERROR_MESSAGE);

        }
    }
}
