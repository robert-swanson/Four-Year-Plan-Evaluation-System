package preferences.explanation.context;

import preferences.context.Context;
import preferences.explanation.Explanation;

import java.util.LinkedHashMap;

public class ContextExplanation extends Explanation {
    LinkedHashMap<String, TermSubContextExplanation> terms;
    public ContextExplanation(Context context) {
        super(context);
        terms = new LinkedHashMap<>();
        context.getTermSubContexts().forEach((key, value) -> terms.put(key.toString(), value.explainLastResult()));
    }
}
