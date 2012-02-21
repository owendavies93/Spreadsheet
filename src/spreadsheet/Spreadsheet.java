package spreadsheet;

import java.util.HashMap;
import java.util.Map;
import spreadsheet.api.CellLocation;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.api.value.*;

public class Spreadsheet implements SpreadsheetInterface {

    private Map<CellLocation, Cell> locations =
            new HashMap<CellLocation, Cell>();

    @Override
    public void setExpression(CellLocation location, String expr) {
        if (locations.get(location) != null) {
            Cell c = locations.get(location);
            c.setExpr(expr);
            c.setVal(new StringValue(expr));
        } else {
            Cell c = new Cell(this, location, expr);
            c.setVal(new StringValue(expr));
            locations.put(location, c);
        }
    }

    @Override
    public String getExpression(CellLocation location) {
        Cell c = locations.get(location);
        return c != null ? c.getExpr() : "";
    }

    @Override
    public Value getValue(CellLocation location) {
        Cell c = locations.get(location);
        return c != null ? c.getVal() : new InvalidValue("");
    }

    @Override
    public void recompute() {
        // TODO Auto-generated method stub

    }
}
