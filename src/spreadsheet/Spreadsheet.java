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
            checkCycle(c, new HashSet<Cell>());

            if (!c.isInLoop()) {
                if (!ignore.contains(c)) {
                    recomputeCell(c);
                }
            } else {
                c.setVal(LoopValue.INSTANCE);
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

    private void checkCycle(Cell c, Set<Cell> visited) {
        if (visited.contains(c)) {
            c.setInLoop(true);
        } else {
            visited.add(c);

            for (Cell child : getChildren(c)) {
                checkCycle(child, visited);
            }

            visited.remove(c);
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
