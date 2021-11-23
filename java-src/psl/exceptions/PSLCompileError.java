package psl.exceptions;

import org.antlr.v4.runtime.Token;

public class PSLCompileError extends Exception {

    public PSLCompileError(String message, Token token) {
        super(token == null ? message : String.format("%d:%d: %s", token.getLine(), token.getCharPositionInLine(), message));
    }

    public static class FileDoesNotExistError extends PSLCompileError {
        public FileDoesNotExistError(String file) {
            super(String.format("File does not exist at \"%s\"", file), null);
        }
    }

    public static class PriorityRedeclarationError extends PSLCompileError {
        public PriorityRedeclarationError(Token token, String name) {
            super(String.format("Priority \"%s\" already defined", name), token);
        }
    }
}
