package spreadsheet;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import spreadsheet.api.CellLocation;
import spreadsheet.api.ExpressionUtils;
import spreadsheet.api.observer.Observer;
import spreadsheet.api.value.InvalidValue;
import spreadsheet.api.value.LoopValue;
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
        this.inLoop = false;
    }

    public final Set<Cell> getReferences() {
        return thisReferences;
    }

    public final CellLocation getLoc() {
        return loc;
    }

    public final Value getVal() {
        return val;
    }

    public final void setVal(Value val) {
        if (inLoop) {
            this.val = LoopValue.INSTANCE;
        } else {
            this.val = val;
        }
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

        LinkedHashSet<Cell> seen = new LinkedHashSet<Cell>();
        checkLoops(this, seen);

        setVal(new InvalidValue(newExpr));

        if (!inLoop) {

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

    private void checkLoops(Cell c, LinkedHashSet<Cell> seen) {
        if (seen.contains(c)) {
            markAsLoop(c, seen);
        } else {
            seen.add(c);
            LinkedHashSet<Cell> children = sheet.getChildren(c);

            for (Cell child : children) {
                checkLoops(child, seen);
            }
            seen.remove(c);
        }
    }

    private void markAsLoop(Cell cell, LinkedHashSet<Cell> seen) {
        cell.inLoop = true;

        boolean seenStart = false;
        for (Cell c : seen) {
            if (c.getLoc().equals(cell.getLoc())) {
                seenStart = true;
            }
            if (seenStart) {
                c.inLoop = true;
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
