package exceptions;

public class CatalogException extends Exception {
    public CatalogException(String message) {
        super(message);
    }

    public static class CourseNotFoundException extends CatalogException {
        public CourseNotFoundException(String message) {
            super(message);
        }
    }
}
