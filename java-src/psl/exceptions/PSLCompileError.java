package psl.exceptions;

import org.antlr.v4.runtime.Token;

public class PSLCompileError extends RuntimeException {

    public PSLCompileError(String message, Token token) {
        super(token == null ? String.format("ERROR: %s", message) : String.format("ERROR %d:%d: %s", token.getLine(), token.getCharPositionInLine(), message));
    }

    public static void assertTrue(boolean assertion, String message) {
        if (!assertion) {
            throw new AssertionError(message, null);
        }
    }

    public static void assertTrue(boolean assertion, String message, Token token) {
        if (!assertion) {
            throw new AssertionError(message, token);
        }
    }

    public static class AssertionError extends PSLCompileError {
        public AssertionError(String message, Token token) {
            super(String.format("Assertion Error: %s", message), null);
        }
    }

    public static class FileDoesNotExistError extends PSLCompileError {
        public FileDoesNotExistError(String file) {
            super(String.format("File does not exist at \"%s\"", file), null);
        }
    }

    public static class PriorityRedeclarationError extends PSLCompileError {
        public PriorityRedeclarationError(String name, Token token) {
            super(String.format("Priority \"%s\" already defined", name), token);
        }
    }

    public static class NonExistentImportError extends  PSLCompileError {
        public NonExistentImportError(String symbol, Token token) {
            super(String.format("Import to non-existent block '%s'", symbol), token);
        }
    }

    public static class DuplicateSymbolDefinitionError extends  PSLCompileError {
        public DuplicateSymbolDefinitionError(String symbol, Token token) {
            super(String.format("Duplicate symbol definition: '%s'", symbol), token);
        }
    }
}
