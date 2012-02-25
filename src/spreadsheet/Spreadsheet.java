package spreadsheet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import spreadsheet.api.CellLocation;
import spreadsheet.api.ExpressionUtils;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.api.value.*;

public class Spreadsheet implements SpreadsheetInterface {

    private Map<CellLocation, Cell> locations =
            new HashMap<CellLocation, Cell>();

    private Set<Cell> invalid = new HashSet<Cell>();

    @Override
    public void setExpression(CellLocation location, String expr) {
        if (getCellAt(location) != null) {
            Cell c = getCellAt(location);
            if (!invalid.contains(c)) {
                c.setExpr(expr);
            }
            c.setVal(new InvalidValue(expr)); // You changed this
        } else {
            Cell c = new Cell(this, location);
            c.setExpr(expr);
            c.setVal(new InvalidValue(expr)); // and this
            locations.put(location, c);
        }
    }

    @Override
    public String getExpression(CellLocation location) {
        Cell c = getCellAt(location);
        return c != null ? c.getExpr() : "";
    }

    @Override
    public Value getValue(CellLocation location) {
        Cell c = getCellAt(location);
        return c != null ? c.getVal() : null;
    }

    @Override
    public void recompute() {
        Iterator<Cell> i = invalid.iterator();
        while (i.hasNext()) {
            Cell c = i.next();
            recomputeCell(c);
            i.remove();
        }
    }

    private void recomputeCell(Cell c) {
        LinkedHashSet<Cell> seen = new LinkedHashSet<Cell>();
        seen.add(c);
        checkLoops(c, seen);
        if (c.getVal() != LoopValue.INSTANCE) {
            c.setVal(new StringValue(c.getExpr()));
        }
    }

    private void checkLoops(Cell c, LinkedHashSet<Cell> cellsSeen) {
        Set<CellLocation> locs =
                ExpressionUtils.getReferencedLocations(c.getExpr());

        for (CellLocation l : locs) {
            Cell child = getCellAt(l);
            if (cellsSeen.contains(child)) {
                markAsLoop(child, cellsSeen);
            } else {
                cellsSeen.add(child);
                checkLoops(child, cellsSeen);
            }
        }
    }

    private void markAsLoop(Cell startCell, LinkedHashSet<Cell> cells) {
        invalid.removeAll(cells);
        startCell.setVal(LoopValue.INSTANCE);
        // TODO - gives all the cells in cell from startCell (inclusive) a
        // LoopValue
    }

    public Set<Cell> getInvalid() {
        return invalid;
    }

    public Cell getCellAt(CellLocation loc) {
        return locations.get(loc);
    }

}
