package spreadsheet.api.value;

/**
 * Visitor interface for values.
 */
public interface ValueVisitor {
    
    public void visitDouble(double value);
    
    public void visitLoop();
    
    public void visitString(String expression);

    public void visitInvalid(String expression);
}
