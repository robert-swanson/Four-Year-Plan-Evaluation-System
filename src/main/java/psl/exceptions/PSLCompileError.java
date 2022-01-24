package psl.exceptions;

import org.antlr.v4.runtime.Token;

public class PSLCompileError extends RuntimeException {

    public PSLCompileError(String message) {
        super(message);
    }

    public static void assertTrue(boolean assertion, String message) {
        if (!assertion) {
            throw new AssertionError(message, null);
        }
    }

    public static class AssertionError extends PSLCompileError {
        public AssertionError(String message, Token token) {
            super(String.format("Assertion Error: %s", message));
        }
    }

    public static class FileDoesNotExistError extends PSLCompileError {
        public FileDoesNotExistError(String file) {
            super(String.format("File does not exist at \"%s\"", file));
        }
    }

}
