package spreadsheet;

import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.gui.SpreadsheetGUI;

public class Main {

    private static final int DEFAULT_NUM_ROWS = 5000;
    private static final int DEFAULT_NUM_COLUMNS = 5000;

    public static void main(String[] args) {
        SpreadsheetInterface s = new Spreadsheet();
        SpreadsheetGUI gui;

        if (args.length == 2) {
            gui =
                    new SpreadsheetGUI(s, Integer.parseInt(args[0]),
                            Integer.parseInt(args[1]));
        } else {
            gui = new SpreadsheetGUI(s, DEFAULT_NUM_ROWS, DEFAULT_NUM_COLUMNS);
        }

        gui.start();
    }
}
