package preferences.value;

import java.util.Objects;

public class TextValue extends Value {
    private final String value;

    public TextValue(String s) {
        value = s;
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextValue text = (TextValue) o;
        return Objects.equals(value, text.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
