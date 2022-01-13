package tests;

import preferences.specification.FullSpecification;
import psl.PSLCompiler;
import psl.exceptions.PSLCompileError;

public class PSLCompileTest {
    public static void main(String[] args) {
        String[] paths = {
                "test-input/full-test.psl",
                "test-input/test.psl",
                "test-input/my-preferences.psl",
                "test-input/simple.psl",
        };
        int offset = 0;

        for (int i = 0; i < paths.length; i++) {
            int index = (i+offset)%paths.length;
            System.out.printf("%d,", i);
            timePSLParse(paths[index]);
        }
        for (String path : paths) {
            for (int i = 0; i < 4; i++) {
                timePSLParse(path);
            }
        }
    }

    private static int timePSLParse(String filePath) {
        try {
            PSLCompiler compiler = new PSLCompiler();
            long startTime = System.nanoTime();
            FullSpecification compiledSpecification = compiler.compile(filePath);
            long endTime = System.nanoTime();
            System.out.printf("%s,%.2f\n", filePath, (endTime-startTime)/1000000.0);
            return (int)(endTime-startTime);
        } catch (PSLCompileError e) {
            e.printStackTrace();
            return -1;
        }
    }
}
