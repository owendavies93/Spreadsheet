package spreadsheet.api.value;

/**
 * A value for a spreadsheet cell that contains an uninterpreted String.
 */
public final class StringValue implements Value {

    private final String expression;

    public StringValue(String expression) {
        this.expression = expression;
    }

    @Override
    public void visit(ValueVisitor visit) {
        visit.visitString(expression);
    }

    @Override
    public String toString() {
        return expression;
    }
}
