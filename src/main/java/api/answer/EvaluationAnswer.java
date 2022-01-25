package api.answer;

import api.instance.AppInstance;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import preferences.specification.FullSpecification;

import java.io.FileWriter;
import java.io.IOException;

public class EvaluationAnswer {
    JsonArray answerList;

    public EvaluationAnswer() {
        answerList = new JsonArray();
    }

    public void addAnswer(String planID, FullSpecification answer, boolean explain) {
        JsonObject thisAnswer = new JsonObject();

        JsonElement score = AppInstance.createGson().toJsonTree(answer.getLastResult());
        thisAnswer.add("lastScore", score);
        thisAnswer.addProperty("id", planID);

        if (explain) {
            JsonElement explanation = AppInstance.createGson().toJsonTree(answer);
            thisAnswer.add("explanation", explanation);
        }

        answerList.add(thisAnswer);
    }

    public void writeToFile(String filePath) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath, false);
        fileWriter.write(AppInstance.createGson().toJson(answerList));
        fileWriter.close();
    }

    public String toJSON() {
        return answerList.getAsString();
    }

}
