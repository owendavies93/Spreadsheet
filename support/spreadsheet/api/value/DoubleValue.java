package spreadsheet.api.value;

/**
 * A value for spreadsheet cells that represented an expression that evaluated
 * to a double.
 * 
 */
public final class DoubleValue implements Value {

    private final double value;

    public DoubleValue(double d) {
        this.value = d;
    }

    @Override
    public void visit(ValueVisitor visit) {
        visit.visitDouble(value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

}
