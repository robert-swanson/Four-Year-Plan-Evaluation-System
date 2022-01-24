import api.PSLInstance;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import preferences.specification.FullSpecification;

import java.util.ArrayList;

public class Main {
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
            PSLInstance pslInstance = new PSLInstance(catalogPath, offeringsPath);
            dependencies.forEach(pslInstance::addDependencyFile);
            if (!pslPath.equals("")) {
                pslInstance.loadPSLFile(pslPath);
            } else {
                pslInstance.loadPSLJsonFile(pslJSON);
            }
            FullSpecification specification = pslInstance.evaluatePlanFile(planPath);
//            explanation.writeToFile(outPath);
            specification.writeToFile(outPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
