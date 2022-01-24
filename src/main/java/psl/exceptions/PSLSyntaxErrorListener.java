package psl.exceptions;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.LinkedList;

public class PSLSyntaxErrorListener extends BaseErrorListener {
    LinkedList<PSLSyntaxError> exceptionLog = new LinkedList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) throws ParseCancellationException {
        exceptionLog.add(new PSLSyntaxError(msg, e.getOffendingToken()));
        throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
    }

    public LinkedList<PSLSyntaxError> getExceptionLog() {
        return exceptionLog;
    }
}
