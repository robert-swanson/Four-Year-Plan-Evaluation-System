package psl.exceptions;

import psl.listener.PSLParsingContext;

public class ListenerError extends RuntimeException {
    public ListenerError(String message) {
        super(message);
    }
}
