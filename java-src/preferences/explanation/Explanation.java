package preferences.explanation;

import api.PSLInstance;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;

public abstract class Explanation {
    String description;
    public Explanation(Explainable explainable) {
        if (explainable == null) {
            this.description = "not computed";
        } else {
            this.description = explainable.describe();
        }
    }

    public static class NotComputed extends Explanation {
        public NotComputed() {
            super(null);
        }
    }

    public void writeToFile(String filePath) throws IOException {
        Gson gson = PSLInstance.createGson();
        String result = gson.toJson(this);
        FileWriter fileWriter = new FileWriter(filePath, false);
        fileWriter.write(result);
        fileWriter.close();
    }
}
