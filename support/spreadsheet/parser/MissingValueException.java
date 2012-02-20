package spreadsheet.parser;

public class MissingValueException extends Exception {

    private static final long serialVersionUID = 1L;

    public MissingValueException(String message) {
        super(message);
    }
}
