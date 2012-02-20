package spreadsheet.api.observer;

/**
 * The Observer interface indicates a class can be updated when things of type T
 * change status.
 */
public interface Observer<T> {

    public void update(T changed);

}
