package exceptions;

public class OfferingException extends Exception {
    public OfferingException(String message) {
        super(message);
    }

    public static class CourseOfferingLinkingException extends OfferingException {
        public CourseOfferingLinkingException(String message) {
            super(message);
        }
    }
}
