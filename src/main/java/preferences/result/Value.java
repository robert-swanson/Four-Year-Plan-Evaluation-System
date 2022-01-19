package preferences.result;

import java.util.Objects;

public abstract class Value {
    public static class Text extends Value {
        private final String value;

        public Text(String s) {
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
            Text text = (Text) o;
            return Objects.equals(value, text.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static Boolean TRUE = new Boolean(true);
    public static Boolean FALSE = new Boolean(false);

    public static class Boolean extends Value {
        public final boolean value;

        Boolean(boolean value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value ? "True" : "False";
        }
    }

    public static Null NULL = new Null();

    public static class Null extends Value {
        Null() { }
        @Override
        public String toString() {
            return "Null";
        }
    }
}
