package psl.exceptions;

import org.antlr.v4.runtime.Token;

public class LinkingError extends PSLCompileError {
    public LinkingError(String symbol) {
        super(String.format("Error linking \"%s\": couldn't find labeled block anywhere in dependencies", symbol), null);
    }
}
