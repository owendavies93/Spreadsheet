package spreadsheet;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import spreadsheet.api.CellLocation;
import spreadsheet.api.ExpressionUtils;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.api.value.InvalidValue;
import spreadsheet.api.value.LoopValue;
import spreadsheet.api.value.Value;
import spreadsheet.api.value.ValueVisitor;

public class Spreadsheet implements SpreadsheetInterface {

    private Map<CellLocation, Cell> locations =
            new HashMap<CellLocation, Cell>();

    private Set<Cell> invalid = new HashSet<Cell>();

    @Override
    public final void setExpression(CellLocation location, String expr) {
        if (getCellAt(location) != null) {
            Cell c = getCellAt(location);
            if (!invalid.contains(c)) {
                c.setExpr(expr);
            }
            c.setVal(new InvalidValue(expr));
        } else {
            Cell c = new Cell(this, location);
            c.setExpr(expr);
            c.setVal(new InvalidValue(expr));
            locations.put(location, c);
        }
    }

    @Override
    public final String getExpression(CellLocation location) {
        Cell c = getCellAt(location);
        return c != null ? c.getExpr() : "";
    }

    @Override
    public final Value getValue(CellLocation location) {
        Cell c = getCellAt(location);
        return c != null ? c.getVal() : null;
    }

    @Override
    public final void recompute() {
        Iterator<Cell> i = invalid.iterator();
        while (i.hasNext()) {
            Cell c = i.next();
            recomputeCell(c);
            i.remove();
        }
    }

    private void recomputeCell(Cell c) {
        LinkedHashSet<Cell> seen = new LinkedHashSet<Cell>();
        checkLoops(c, seen);
        if (c.getVal() != LoopValue.INSTANCE) {
            Set<CellLocation> refs =
                    ExpressionUtils.getReferencedLocations(c.getExpr());

            for (CellLocation l : refs) {
                Cell child = getCellAt(l);
                if (child.getVal() == LoopValue.INSTANCE) {
                    c.setVal(new InvalidValue(c.getExpr()));
                    return;
                }
            }

            Deque<Cell> q = new ArrayDeque<Cell>();
            q.addFirst(c);

            while (!q.isEmpty()) {
                Cell curr = q.removeFirst();
                Set<CellLocation> currRefs =
                        ExpressionUtils.getReferencedLocations(curr.getExpr());

                int childrenToAdd = 0;

                for (CellLocation l : currRefs) {
                    Cell currChild = getCellAt(l);
                    if (invalid.contains(currChild)) {
                        childrenToAdd++;
                        q.addFirst(currChild);
                    }
                }
                q.addLast(curr);

                if (childrenToAdd == 0) {
                    calculateCellValue(curr, currRefs);
                    q.remove();
                }

                // if (q.isEmpty() && childrenToAdd != 0) {
                // System.out.println("ARGH");
                // }
            }
        }
    }

    private void calculateCellValue(Cell c, Set<CellLocation> refs) {
        final Map<CellLocation, Double> vals =
                new HashMap<CellLocation, Double>();

        for (CellLocation l : refs) {
            final Cell child = getCellAt(l);
            child.getVal().visit(new ValueVisitor() {

                @Override
                public void visitDouble(double v) {
                    vals.put(child.getLoc(), v);
                }

                @Override
                public void visitLoop() {
                }

                @Override
                public void visitString(String expression) {
                }

                @Override
                public void visitInvalid(String expression) {
                }

            });
        }

        c.setVal(ExpressionUtils.computeValue(c.getExpr(), vals));
    }

    private void checkLoops(Cell c, LinkedHashSet<Cell> cellsSeen) {
        if (cellsSeen.contains(c)) {
            markAsLoop(c, cellsSeen);
        } else {
            cellsSeen.add(c);
            for (Cell child : c.getReferences()) {
                checkLoops(child, cellsSeen);
            }
            cellsSeen.remove(c);
        }
    }

    private void markAsLoop(Cell startCell, LinkedHashSet<Cell> cells) {
        startCell.setVal(LoopValue.INSTANCE);

        boolean seenStart = false;
        for (Cell c : cells) {
            if (c.getLoc().equals(startCell.getLoc())) {
                seenStart = true;
            }
            if (seenStart) {
                c.setVal(LoopValue.INSTANCE);
            }
        }
    }

    public final Set<Cell> getInvalid() {
        return invalid;
    }

    public final Cell getCellAt(CellLocation loc) {
        return locations.get(loc);
    }

}
