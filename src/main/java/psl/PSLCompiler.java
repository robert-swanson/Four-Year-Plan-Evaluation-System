package psl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import preferences.specification.FullSpecification;
import preferences.specification.Specification;
import preferences.specification.SpecificationList;
import psl.antlr.PSLGrammarLexer;
import psl.antlr.PSLGrammarParser;
import psl.exceptions.PSLCompileError;
import psl.exceptions.PSLParsingError;
import psl.exceptions.PSLSyntaxErrorListener;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class PSLCompiler {
    private final HashMap<String, Specification> dependencies;

    PSLGrammarLexer lexer;
    CommonTokenStream tokenStream;
    PSLGrammarParser parser;

    public PSLCompiler() {
        dependencies = new HashMap<>();
    }

    public void addDependencyPSLString(String dependencyPSL) throws PSLCompileError {
        HashMap<String, Specification> importableSpecifications;
        importableSpecifications = compilePSLString(dependencyPSL);
        importableSpecifications.forEach((s, specification) -> {
            if (dependencies.containsKey(s)) {
                throw new PSLParsingError.DuplicateSymbolDefinitionError(s, null);
            }
            dependencies.put(s, specification);
        });
    }

    public void addDependencyFile(String dependencyFile) throws PSLCompileError {
        HashMap<String, Specification> importableSpecifications = compilePSLFile(dependencyFile);
        importableSpecifications.forEach((s, specification) -> {
            if (dependencies.containsKey(s)) {
                throw new PSLParsingError.DuplicateSymbolDefinitionError(s, null);
            }
            dependencies.put(s, specification);
        });
    }

    public FullSpecification compileFile(String filename) throws PSLCompileError {
        String name = new File(filename).getName();
        LinkedHashMap<String, Specification> blocks = compilePSLFile(filename);
        SpecificationList specificationList = new SpecificationList();
        blocks.values().forEach(specificationList::addSpecification);
        return new FullSpecification(specificationList.getSimplifiedSpecification(), name);
    }

    public FullSpecification compileString(String psl) {
        LinkedHashMap<String, Specification> blocks = compilePSLString(psl);
        SpecificationList specificationList = new SpecificationList();
        blocks.values().forEach(specificationList::addSpecification);
        return new FullSpecification(specificationList.getSimplifiedSpecification(), "PSL from string");
    }

    private LinkedHashMap<String, Specification> compilePSLString(String psl) {
        InputStream stream = new ByteArrayInputStream(psl.getBytes(StandardCharsets.UTF_8));
        CharStream charStream;
        try {
            charStream = CharStreams.fromStream(stream);
        } catch (IOException e) {
            throw new PSLCompileError("error converting string to stream");
        }
        return compilePSLStream(charStream);
    }

    private LinkedHashMap<String, Specification> compilePSLFile(String filename) throws PSLCompileError {
        CharStream charStream;
        try {
            charStream = CharStreams.fromFileName(filename);
            return compilePSLStream(charStream);
        } catch (IOException e) {
            throw new PSLCompileError.FileDoesNotExistError(filename);
        }
    }

    private LinkedHashMap<String, Specification> compilePSLStream(CharStream charStream) throws PSLCompileError {
        lexer = new PSLGrammarLexer(charStream);
        tokenStream = new CommonTokenStream(lexer);
        parser = new PSLGrammarParser(tokenStream);

        PSLListener listener = new PSLListener(dependencies);
        PSLSyntaxErrorListener errorListener = new PSLSyntaxErrorListener();

        parser.addParseListener(listener);
        parser.addErrorListener(errorListener);

        try {
            parser.start();
        } catch (Exception e) {
            int numErrors = errorListener.getExceptionLog().size();
            if (numErrors == 0) {
                throw e;
            } else {
                throw errorListener.getExceptionLog().get(0);
            }
        }
        return listener.getBlocks();

    }
}
