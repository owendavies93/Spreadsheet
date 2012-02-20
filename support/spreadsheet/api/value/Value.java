package spreadsheet.api.value;

/**
 * Interface representing the values that a particular spreadsheet cell can
 * take.
 */
public interface Value {

    public void visit(ValueVisitor visitor);

}
