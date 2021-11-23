package psl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import preferences.specification.Specification;
import preferences.specification.SpecificationList;
import psl.exceptions.LinkingError;
import psl.exceptions.PSLCompileError;
import psl.listener.PSLListener;

import java.io.IOException;
import java.util.*;

public class PSLCompiler {
    private String filename;
    private String[] dependencies;
    private HashMap<String, Specification> symbols;
    private ArrayList<SpecificationList> unresolvedSymbolSpecificationLists;
    private ArrayList<String> unresolvedSymbols;
    private HashMap<String, Double> priorities;

    public PSLCompiler(String filename, String[] dependencies) {
        this.filename = filename;
        this.dependencies = dependencies;

        symbols = new HashMap<>();
        unresolvedSymbolSpecificationLists = new ArrayList<>();
        unresolvedSymbols = new ArrayList<>();
        priorities = new HashMap<>();
    }

    public Specification compile() throws PSLCompileError {
        HashMap<String, Specification> blocks = compileFile(filename);
        SpecificationList specificationList = new SpecificationList();
        blocks.values().forEach(specificationList::addSpecification);
        return specificationList.getSimplifiedSpecification();
    }

    private HashMap<String,Specification> compileFile(String filename) throws PSLCompileError {
        CharStream charStream;
        try {
            charStream = CharStreams.fromFileName(filename);
        } catch (IOException e) {
            throw new PSLCompileError.FileDoesNotExistError(filename);
        }

        PSLGrammarLexer lexer = new PSLGrammarLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        PSLGrammarParser parser = new PSLGrammarParser(tokenStream);

        PSLListener listener = new PSLListener();
        parser.addParseListener(listener);
        parser.start();
        return listener.getBlocks();
    }

    private void linkSymbols() throws LinkingError {
        Iterator<SpecificationList> specificationListIterator = unresolvedSymbolSpecificationLists.listIterator();
        Iterator<String> symbolIterator = unresolvedSymbols.iterator();

        while (specificationListIterator.hasNext() && symbolIterator.hasNext()) {
            SpecificationList specificationList = specificationListIterator.next();
            String symbol = symbolIterator.next();
            if (symbols.containsKey(symbol)) {
                specificationList.addSpecification(symbols.get(symbol));
            } else {
                throw new LinkingError(symbol);
            }
        }
    }
}
