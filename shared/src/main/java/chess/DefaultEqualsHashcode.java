package chess;

public class DefaultEqualsHashcode {
    public boolean equals(Object other) {
        if (this == other) {return true;}
        if (null == other || getClass() != other.getClass()) {return false;}
        return toString().equals(other.toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }
}
