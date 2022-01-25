package preferences.explanation;

import api.instance.AppInstance;
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
        FileWriter fileWriter = new FileWriter(filePath, false);
        fileWriter.write(toJSON());
        fileWriter.close();
    }

    public String toJSON(){
        Gson gson = AppInstance.createGson();
        return gson.toJson(this);
    }
}
