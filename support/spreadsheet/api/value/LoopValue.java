package spreadsheet.api.value;

/**
 * A value for spreadsheet cells that are part of a loop.
 */
public final class LoopValue implements Value {

    public static final LoopValue INSTANCE = new LoopValue(); 

    private LoopValue() { 
        // not visible
    }
    @Override
    public void visit(ValueVisitor visit) {
        visit.visitLoop();
    }

    @Override
    public String toString() {
        return "#LOOP";
    }
}
