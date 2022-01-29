package api.instance;

import api.EvaluationAnswer;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import preferences.specification.FullSpecification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class LocalInstance extends AppInstance {
    public LocalInstance(String catalogFilename, String offeringsFilename) throws Exception {
        super();
        loadCatalogString(Files.readString(Path.of(catalogFilename)));
        loadOfferingsString(Files.readString(Path.of(offeringsFilename)));
        linkCourses();
    }

    public void addDependencyFile(String dependencyFilename) throws IOException {
        addDependencyString(Files.readString(Path.of(dependencyFilename)));
    }

    public FullSpecification loadPSLFile(String pslFilename) throws IOException {
        return loadPSLString(Files.readString(Path.of(pslFilename)));
    }

    public FullSpecification loadPSLJSONFile(String pslJSONFilename) throws IOException {
        return loadPSLJsonString(Files.readString(Path.of(pslJSONFilename)));
    }

    public EvaluationAnswer evaluatePlansFile(String plansFilename, FullSpecification specification, boolean includeExplanation) throws Exception {
        return evaluatePlansString(Files.readString(Path.of(plansFilename)), specification, includeExplanation);
    }

    public static void main(String[] args) {
        String catalogPath = "", offeringsPath = "", planPath = "", pslPath = "", pslJSON = "", outPath = "";
        ArrayList<String> dependencies = new ArrayList<>();

        LongOpt catalogOpt = new LongOpt("catalog", LongOpt.REQUIRED_ARGUMENT, null, 'c');
        LongOpt offeringsOpt = new LongOpt("offerings", LongOpt.REQUIRED_ARGUMENT, null, 'o');
        LongOpt planOpt = new LongOpt("plan", LongOpt.REQUIRED_ARGUMENT, null, 'p');
        LongOpt pslOpt = new LongOpt("psl", LongOpt.REQUIRED_ARGUMENT, null, 'l');
        LongOpt pslJSONOpt = new LongOpt("psl-json", LongOpt.REQUIRED_ARGUMENT, null, 'l');
        LongOpt depOpt = new LongOpt("dependency", LongOpt.REQUIRED_ARGUMENT, null, 'd');
        LongOpt outOpt = new LongOpt("out", LongOpt.REQUIRED_ARGUMENT, null, 't');
        LongOpt[] longOpts = new LongOpt[]{catalogOpt, offeringsOpt, planOpt, pslOpt, pslJSONOpt, depOpt, outOpt};

        Getopt g = new Getopt("main", args, "", longOpts);
        while (g.getopt() != -1) {
            switch (g.getLongind()) {
                case 0 -> catalogPath = g.getOptarg();
                case 1 -> offeringsPath = g.getOptarg();
                case 2 -> planPath = g.getOptarg();
                case 3 -> pslPath = g.getOptarg();
                case 4 -> pslJSON = g.getOptarg();
                case 5 -> dependencies.add(g.getOptarg());
                case 6 -> outPath = g.getOptarg();
                default -> System.out.printf("Unrecognized option: '%s'\n", g.getOptopt());
            }
        }

        try {
            LocalInstance instance = new LocalInstance(catalogPath, offeringsPath);
            for (String dep : dependencies) instance.addDependencyFile(dep);

            FullSpecification specification;
            if (!pslPath.equals("")) {
                specification = instance.loadPSLFile(pslPath);
            } else {
                specification = instance.loadPSLJSONFile(pslJSON);
            }
            specification.writePSL("assets/psl/generated.psl");
            EvaluationAnswer answer = instance.evaluatePlansFile(planPath, specification, true);
            answer.writeToFile(outPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
