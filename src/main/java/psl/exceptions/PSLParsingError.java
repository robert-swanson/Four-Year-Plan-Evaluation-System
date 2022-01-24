package psl.exceptions;

import org.antlr.v4.runtime.Token;

public class PSLParsingError extends PSLCompileError {

    public PSLParsingError(String message, Token token) {
        super(token == null ? message : String.format("%d:%d: %s", token.getLine(), token.getCharPositionInLine(), message));
    }

    public static void assertTrue(boolean assertion, String message, Token token) {
        if (!assertion) {
            throw new AssertionError(message, token);
        }
    }

    public static class PriorityRedeclarationError extends PSLParsingError {
        public PriorityRedeclarationError(String name, Token token) {
            super(String.format("Priority \"%s\" already defined", name), token);
        }
    }

    public static class NonExistentImportError extends  PSLParsingError {
        public NonExistentImportError(String symbol, Token token) {
            super(String.format("Import to non-existent block '%s'", symbol), token);
        }
    }

    public static class DuplicateSymbolDefinitionError extends  PSLParsingError {
        public DuplicateSymbolDefinitionError(String symbol, Token token) {
            super(String.format("Duplicate symbol definition: '%s'", symbol), token);
        }
    }

}
