package spreadsheet.api;

/**
 * Represents a cell's location in the spreadsheet.
 */
public final class CellLocation {

    private final String representation;

    public CellLocation(String representation) {
        if (representation == null) {
            throw new NullPointerException("representation cannot be null");
        }
        this.representation = representation;
    }

    public String toString() {
        return representation;
    }

    @Override
    public int hashCode() {
        return representation.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CellLocation other = (CellLocation) obj;
        if (representation == null) {
            if (other.representation != null) {
                return false;
            }
        } else if (!representation.equals(other.representation)) {
            return false;
        }
        return true;
    }

}
