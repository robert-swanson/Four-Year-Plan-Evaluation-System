package preferences.specification;

import api.PSLInstance;
import com.google.gson.Gson;
import preferences.context.Context;
import preferences.explanation.specification.FullSpecificationResultExplanation;
import preferences.scoring.Score;

import java.io.FileWriter;
import java.io.IOException;

public class FullSpecification extends Specification {
    Specification specification;
    Context lastContext;
    String name;

    public FullSpecification(Specification specification, String name) {
        this.specification = specification;
        this.name = name;
    }

    public FullSpecification(String json) {
        Gson gson = PSLInstance.createGson();
        this.specification = gson.fromJson(json, FullSpecification.class);
        this.name = "from JSON";
    }

    public void writeToFile(String filePath) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath, false);
        fileWriter.write(toJSON());
        fileWriter.close();
    }

    public String toJSON() {
        Gson gson = PSLInstance.createGson();
        return gson.toJson(this);
    }


    @Override
    public Score evaluate(Context context, boolean evaluateAll) {
        lastContext = context;
        lastScore = specification.evaluate(context, evaluateAll);
        return lastScore;
    }

    @Override
    public FullSpecificationResultExplanation explainLastResult() {
        return new FullSpecificationResultExplanation(this, specification, lastContext);
    }

    @Override
    public String describe() {
        return name;
    }

    @Override
    public Specification getSimplifiedSpecification() {
        return new FullSpecification(specification.getSimplifiedSpecification(), name);
    }
}
