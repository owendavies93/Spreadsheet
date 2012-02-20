package spreadsheet.api.value;

/**
 * A value for spreadsheet cells that are currently invalid.
 * 
 */
public final class InvalidValue implements Value {

    private final String expression;

    public InvalidValue(String expression) {
        this.expression = expression;
    }

    @Override
    public void visit(ValueVisitor visitor) {
        visitor.visitInvalid(expression);
    }

    public String toString() {
        return "{" + expression + "}";
    }

}
