package spreadsheet;

import spreadsheet.api.CellLocation;
import spreadsheet.api.value.InvalidValue;
import spreadsheet.api.value.Value;

public class Cell {

    private Spreadsheet sheet;
    private CellLocation loc;
    private Value val;
    private String expr;

    public Cell(Spreadsheet sheet, CellLocation loc, String expr) {
        this.sheet = sheet;
        this.loc = loc;
        this.setVal(new InvalidValue(""));
        this.setExpr(expr);
    }

    public Value getVal() {
        return val;
    }

    public void setVal(Value val) {
        this.val = val;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

}
