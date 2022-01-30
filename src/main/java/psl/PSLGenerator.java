package psl;

public class PSLGenerator {
    StringBuilder blockBuilder, pslBuilder;
    private int currentIndentation = 0;
    public boolean plural = true;

    public PSLGenerator() {
        blockBuilder = new StringBuilder();
        pslBuilder = new StringBuilder();
    }

    public void indent() {
        currentIndentation++;
    }
    public void dedent() {
        currentIndentation--;
        blockBuilder.delete(blockBuilder.length()-INDENT.length(), blockBuilder.length());
    }

    static final String INDENT = "\t";

    private String getIndent() {
        return "" + INDENT.repeat(Math.max(0, currentIndentation));
    }

    public void addPSL(String psl) {
        blockBuilder.append(psl.replaceAll("\n", "\n"+getIndent()).replaceAll(" {2,}", " "));
    }

    public String numToString(double num) {
        return Double.toString(num).replaceAll("0*$", "").replaceAll("\\.$", "");
    }



    @Override
    public String toString() {
        return blockBuilder.toString();
    }
}
