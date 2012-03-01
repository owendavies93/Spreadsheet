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
    private Set<Cell> ignore = new HashSet<Cell>();

    @Override
    public final void setExpression(CellLocation location, String expr) {
        if (getCellAt(location) != null) {
            Cell c = getCellAt(location);
            if (!invalid.contains(c) || c.getExpr() != expr) {
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

            c.setInvalid(false);
            checkLoops(c, new LinkedHashSet<Cell>());

            if (c.getVal() != LoopValue.INSTANCE) {
                if (c.alwaysInvalid()) {
                    c.setVal(new InvalidValue(c.getExpr()));
                } else if (!ignore.contains(c)) {
                    recomputeCell(c);
                }
            }

            i.remove();
        }
        ignore.clear();
    }

    private void recomputeCell(Cell c) {
        Deque<Cell> q = new ArrayDeque<Cell>();
        q.offer(c);

        while (!q.isEmpty()) {
            Cell node = q.poll();
            LinkedHashSet<Cell> nodeChildren = getChildren(node);

            boolean dependantsRecompute = false;

            for (Cell child : nodeChildren) {
                if (invalid.contains(child) && !ignore.contains(child)) {
                    q.offer(child);
                    dependantsRecompute = true;
                }
            }
            q.offer(node);

            if (!dependantsRecompute) {
                calculateCellValue(node);
                ignore.add(node);
                q.remove(node);
            }
        }
    }

    private void checkLoops(Cell c, LinkedHashSet<Cell> cellsSeen) {
        if (cellsSeen.contains(c)) {
            markAsLoop(c, cellsSeen);
        } else {
            cellsSeen.add(c);

            for (Cell child : getChildren(c)) {
                checkLoops(child, cellsSeen);
            }

            cellsSeen.remove(c);
        }
    }

    private void markAsLoop(Cell startCell, LinkedHashSet<Cell> cells) {
        boolean startReached = false;

        for (Cell c : cells) {
            c.setInvalid(true);

            if (c.getLoc().equals(startCell.getLoc())) {
                startReached = true;
            }

            if (startReached) {
                c.setVal(LoopValue.INSTANCE);
            }
        }
    }

    private void calculateCellValue(Cell c) {
        final Map<CellLocation, Double> vals =
                new HashMap<CellLocation, Double>();

        for (final Cell child : getChildren(c)) {
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

    public final Set<Cell> getInvalid() {
        return invalid;
    }

    public final Cell getCellAt(CellLocation loc) {
        if (locations.get(loc) == null) {
            Cell c = new Cell(this, loc);
            c.setVal(null);
            locations.put(loc, c);
            return c;
        } else {
            return locations.get(loc);
        }
    }

    public final LinkedHashSet<Cell> getChildren(Cell c) {
        Set<CellLocation> refs =
                ExpressionUtils.getReferencedLocations(c.getExpr());
        LinkedHashSet<Cell> children = new LinkedHashSet<Cell>();

        for (CellLocation l : refs) {
            children.add(getCellAt(l));
        }

        return children;
    }

}
