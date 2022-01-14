package exceptions;

public class PSLInstanceException extends Exception {
    public PSLInstanceException(String message) {
        super(message);
    }

    public static class PSLInstanceNotReadyException extends PSLInstanceException {
        public PSLInstanceNotReadyException(String reason) {
            super(String.format("PSL Instance %s", reason));
        }
    }
}
