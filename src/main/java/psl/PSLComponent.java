package psl;

public interface PSLComponent {
    void generatePSL(PSLGenerator generator);

    default String toPSL() {
        PSLGenerator generator = new PSLGenerator();
        generatePSL(generator);
        return generator.toString();
    }
}
