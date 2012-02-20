package spreadsheet.api;

import spreadsheet.api.value.Value;

/**
 * The interface all spreadsheets need to implement
 * 
 */
public interface SpreadsheetInterface {

    /**
     * Sets the expression of the cell at location, and marks the cell as having
     * an InvalidValue. Any cell that depends on the value of that cell should
     * also be marked as having an InvalidValue.
     * 
     * @param location
     *            The location of the cell to update.
     * @param expression
     *            the expression to set the cell to.
     */
    public void setExpression(CellLocation location, String expression);

    /**
     * @return the expression stored at the cell at location.
     */
    public String getExpression(CellLocation location);

    /**
     * @return the value associated with the computed stored expression at
     *         location, or null if there is no value.
     */
    public Value getValue(CellLocation location);

    /**
     * Recomputes the value of all cells which are currently invalid due to an
     * expression changing. This also requires the computation of the values of
     * any dependent cells. Cells forming a loop are assigned a LoopValue.
     * Called after the expression in a cell is modified.
     */
    public void recompute();
}
