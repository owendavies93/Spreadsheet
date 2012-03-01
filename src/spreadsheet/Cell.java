package spreadsheet;

import java.util.HashSet;
import java.util.Set;

import spreadsheet.api.CellLocation;
import spreadsheet.api.ExpressionUtils;
import spreadsheet.api.observer.Observer;
import spreadsheet.api.value.InvalidValue;
import spreadsheet.api.value.Value;

public class Cell implements Observer<Cell> {

    private final Spreadsheet sheet;
    private final CellLocation loc;

    private Value val;
    private String expr;
    private boolean inLoop;

    private Set<Cell> thisReferences = new HashSet<Cell>();
    private Set<Observer<Cell>> referencesMe = new HashSet<Observer<Cell>>();

    public Cell(Spreadsheet sheet, CellLocation loc) {
        this.sheet = sheet;
        this.loc = loc;
        this.setVal(null);
        this.expr = "";
        this.setInLoop(false);
    }

    public final Set<Cell> getReferences() {
        return thisReferences;
    }

    public final CellLocation getLoc() {
        return loc;
    }

    public boolean isInLoop() {
        return inLoop;
    }

    public void setInLoop(boolean inLoop) {
        this.inLoop = inLoop;
    }

    public final Value getVal() {
        return val;
    }

    public final void setVal(Value val) {
        this.val = val;
    }

    public final String getExpr() {
        return expr;
    }

    public final void setExpr(String newExpr) {
        for (Cell c : thisReferences) {
            c.referencesMe.remove(this);
        }
        thisReferences.clear();

        this.expr = newExpr;
        setVal(new InvalidValue(newExpr));

        if (!isInLoop()) {
            addToInvalid();

            Set<CellLocation> locs =
                    ExpressionUtils.getReferencedLocations(newExpr);

            for (CellLocation l : locs) {
                sheet.setExpression(l, sheet.getExpression(l));

                Cell c = sheet.getCellAt(l);
                thisReferences.add(c);
                c.referencesMe.add(this);
            }

            for (Observer<Cell> c : referencesMe) {
                c.update(this);
            }
        }
    }

    @Override
    public final void update(Cell changed) {
        if (!isInInvalidSet()) {
            addToInvalid();
            setVal(new InvalidValue(getExpr()));

            for (Observer<Cell> obs : referencesMe) {
                obs.update(this);
            }
        }
    }

    private final boolean isInInvalidSet() {
        return sheet.getInvalid().contains(this);
    }

    private final void addToInvalid() {
        sheet.getInvalid().add(this);
    }

    public final String toString() {
        return "(" + loc + " -> " + expr + ")";
    }

}
