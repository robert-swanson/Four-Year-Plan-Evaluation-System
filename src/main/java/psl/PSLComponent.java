package psl;

import preferences.value.NumericValue;

public interface PSLComponent {
    void generatePSL(PSLGenerator generator);

    default String toPSL() {
        PSLGenerator generator = new PSLGenerator();
        generatePSL(generator);
        return generator.toString();
    }

    default String toPSL(PSLComponent... context) {
        PSLGenerator generator = new PSLGenerator();
        for (PSLComponent component : context) {
            if (component instanceof NumericValue) {
                if (((NumericValue)component).getValue() == 1) {
                    generator.plural = false;
                }
            }
        }
        generatePSL(generator);
        return generator.toString();
    }
}
