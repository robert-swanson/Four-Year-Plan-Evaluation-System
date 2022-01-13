package psl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import preferences.specification.FullSpecification;
import preferences.specification.Specification;
import preferences.specification.SpecificationList;
import psl.exceptions.PSLCompileError;
import psl.listener.PSLListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class PSLCompiler {
    private final HashMap<String, Specification> dependencies;

    public PSLCompiler() {
        dependencies = new HashMap<>();
    }

    public void addDependency(String dependencyFile) throws PSLCompileError {
        HashMap<String, Specification> importableSpecifications = compileFile(dependencyFile);
        importableSpecifications.forEach((s, specification) -> {
            if (dependencies.containsKey(s)) {
                throw new PSLCompileError.DuplicateSymbolDefinitionError(s, null);
            }
            dependencies.put(s, specification);
        });
    }

    public FullSpecification compile(String filename) throws PSLCompileError {
        String name = new File(filename).getName();
        LinkedHashMap<String, Specification> blocks = compileFile(filename);
        SpecificationList specificationList = new SpecificationList();
        blocks.values().forEach(specificationList::addSpecification);
        return new FullSpecification(specificationList.getSimplifiedSpecification(), name);
    }

    private LinkedHashMap<String, Specification> compileFile(String filename) throws PSLCompileError {
        CharStream charStream;
        try {
            charStream = CharStreams.fromFileName(filename);
        } catch (IOException e) {
            throw new PSLCompileError.FileDoesNotExistError(filename);
        }

        PSLGrammarLexer lexer = new PSLGrammarLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        PSLGrammarParser parser = new PSLGrammarParser(tokenStream);

        PSLListener listener = new PSLListener(dependencies);
        parser.addParseListener(listener);
        parser.start();
        return listener.getBlocks();
    }
}
