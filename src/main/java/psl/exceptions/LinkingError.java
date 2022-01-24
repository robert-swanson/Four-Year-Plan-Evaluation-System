package psl.exceptions;

public class LinkingError extends PSLCompileError {
    public LinkingError(String symbol) {
        super(String.format("Error linking \"%s\": couldn't find labeled block anywhere in dependencies", symbol));
    }
}
