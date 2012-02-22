package spreadsheet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import spreadsheet.api.CellLocation;
import spreadsheet.api.observer.Observer;
import spreadsheet.api.value.InvalidValue;
import spreadsheet.api.value.Value;

public class Cell implements Observer<Cell> {

    private final Spreadsheet sheet;
    private final CellLocation loc;

    private Value val;
    private String expr;

    private final Set<Cell> referees = new HashSet<Cell>();
    private final Set<Observer<Cell>> references =
            new HashSet<Observer<Cell>>();

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
        Iterator<Cell> i = referees.iterator();

        while (i.hasNext()) {
            i.next().removeObserver(this);
        }
    }

    private void removeObserver(Observer<Cell> observer) {
        references.remove(observer);
    }

    @Override
    public void update(Cell changed) {
        // TODO Auto-generated method stub

    }

    public void addToComputeSet() {
        sheet.getChanged().add(this);
    }

    public boolean isInComputeSet() {
        return sheet.getChanged().contains(this);
    }

}
