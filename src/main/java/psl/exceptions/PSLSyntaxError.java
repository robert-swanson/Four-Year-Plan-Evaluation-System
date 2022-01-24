package psl.exceptions;

import org.antlr.v4.runtime.Token;

public class PSLSyntaxError extends PSLParsingError{
    public PSLSyntaxError(String msg, Token token) {
        super(msg, token);
    }
}
