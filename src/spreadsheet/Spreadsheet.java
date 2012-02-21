package spreadsheet;

import spreadsheet.api.CellLocation;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.api.value.Value;
import spreadsheet.api.value.*;

public class Spreadsheet implements SpreadsheetInterface {

    @Override
    public void setExpression(CellLocation location, String expression) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getExpression(CellLocation location) {
        return "";
    }

    @Override
    public Value getValue(CellLocation location) {
        return new InvalidValue("");
    }

    @Override
    public void recompute() {
        // TODO Auto-generated method stub

    }
}
