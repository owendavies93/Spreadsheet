package testsuite.spreadsheet;

import static testsuite.spreadsheet.Util.*;

import org.junit.Test;

import spreadsheet.Spreadsheet;
import spreadsheet.api.CellLocation;

public class TestSetExpression {

    private final CellLocation a1 = new CellLocation("a1");
    private final CellLocation a2 = new CellLocation("a2");
    private final CellLocation b1 = new CellLocation("b1");
    private final CellLocation c1 = new CellLocation("c1");
    private final CellLocation c2 = new CellLocation("c2");
    private final CellLocation d1 = new CellLocation("d1");

    @Test
    public void testSetExpression1() {
        Spreadsheet spreadsheet = new Spreadsheet();

        spreadsheet.setExpression(a1, "=b1+1");
        spreadsheet.setExpression(b1, "=c1+d1");
        spreadsheet.setExpression(c1, "=25");
        spreadsheet.setExpression(c2, "hello");
        spreadsheet.setExpression(d1, "=c1+7");

        spreadsheet.recompute();

        assertIsDouble(spreadsheet.getValue(c1), 25);
        assertIsString(spreadsheet.getValue(c2), "hello");
        assertIsDouble(spreadsheet.getValue(d1), 32);
        assertIsDouble(spreadsheet.getValue(b1), 57);
        assertIsDouble(spreadsheet.getValue(a1), 58);
    }

    @Test
    public void testSetExpression2() {
        Spreadsheet spreadsheet = new Spreadsheet();
        spreadsheet.setExpression(a1, "=a2");
        spreadsheet.setExpression(a2, "=a2");
        spreadsheet.setExpression(b1, "=c1");
        spreadsheet.setExpression(c1, "=d1 - 1");
        spreadsheet.setExpression(d1, "=b1 - 1");
        spreadsheet.recompute();

        assertIsInvalidValue(spreadsheet.getValue(a1), "=a2");
        assertIsLoopValue(spreadsheet.getValue(b1));
        assertIsLoopValue(spreadsheet.getValue(c1));
        assertIsLoopValue(spreadsheet.getValue(d1));
    }

}
