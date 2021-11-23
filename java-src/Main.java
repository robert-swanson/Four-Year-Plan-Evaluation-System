import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.JSONParseException;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import objects.Link;
import objects.misc.*;
import objects.catalog.Catalog;
import objects.offerings.Offerings;
import objects.plan.Plan;
import objects.plan.PlanTerm;
import objects.plan.PlansList;
import preferences.context.Context;
import preferences.explanation.Explanation;
import preferences.scoring.Score;
import preferences.specification.*;
import psl.PSLCompiler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    static Catalog catalog;
    static Offerings offerings;
    static PlansList plansList;

    public static void main(String[] args) {
        String catalogPath = "", offeringsPath = "", planPath = "", pslPath = "", outPath = "";
        LongOpt catalogOpt = new LongOpt("catalog", LongOpt.REQUIRED_ARGUMENT, null, 'c');
        LongOpt offeringsOpt = new LongOpt("offerings", LongOpt.REQUIRED_ARGUMENT, null, 'o');
        LongOpt planOpt = new LongOpt("plan", LongOpt.REQUIRED_ARGUMENT, null, 'p');
        LongOpt pslOpt = new LongOpt("psl", LongOpt.REQUIRED_ARGUMENT, null, 'l');
        LongOpt outOpt = new LongOpt("out", LongOpt.REQUIRED_ARGUMENT, null, 't');
        LongOpt[] longOpts = new LongOpt[]{catalogOpt, offeringsOpt, planOpt, pslOpt, outOpt};

        Getopt g = new Getopt("main", args, "", longOpts);
        while (g.getopt() != -1) {
            switch (g.getLongind()) {
                case 0 -> catalogPath = g.getOptarg();
                case 1 -> offeringsPath = g.getOptarg();
                case 2 -> planPath = g.getOptarg();
                case 3 -> pslPath = g.getOptarg();
                case 4 -> outPath = g.getOptarg();
                default -> System.out.printf("Unrecognized option: '%s'\n", g.getOptopt());
            }
        }

        try {
            parseFiles(catalogPath, offeringsPath, planPath); // Read JSON files and create objects

            System.out.println("- Linking Objects");
            Link.linkObjects(catalog, offerings, plansList); // Link objects and populate maps

            System.out.println("- Creating Context");
            Context context = new Context(plansList.getPlan(0));

            System.out.println("- Compiling Specification");
            PSLCompiler compiler = new PSLCompiler(pslPath, null);
            Specification compiledSpecification = compiler.compile();

            System.out.println("- Scoring Plan");

            long startTime = System.currentTimeMillis();
            compiledSpecification.evaluate(context, true);
            long endTime = System.currentTimeMillis();
            System.out.printf("  - Took %.4fs\n", (endTime-startTime)/1000.0);

            Explanation explanation = compiledSpecification.explainLastResult();
            writeOutput(outPath, explanation);

            context.assertPlanContext();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void writeOutput(String outPath, Explanation explanation) throws IOException {
        GsonBuilder gb = new GsonBuilder();
        registerOutputTypeAdapters(gb);
        Gson gson = gb.setPrettyPrinting().create();

        String result = gson.toJson(explanation);

        FileWriter fileWriter = new FileWriter(outPath, false);
        fileWriter.write(result);
        fileWriter.close();
//        System.out.println(result);

    }

    private static void parseFiles(String catalogPath, String offeringsPath, String planPath) throws JSONParseException {
        GsonBuilder gb = new GsonBuilder();
        registerInputTypeAdapters(gb);
        Gson gson = gb.setPrettyPrinting().create();

        try {
            System.out.println("- Reading Catalog");
            String catalogString = Files.readString(Path.of(catalogPath));
            catalog = gson.fromJson(catalogString, Catalog.class);
        } catch (Exception e) {
            throw new JSONParseException(String.format("Parsing catalog file '%s': %s", catalogPath, e.getMessage()));
        }

        try {
            System.out.println("- Reading Course Offerings");
            String offeringsString = Files.readString(Path.of(offeringsPath)) ;
            offerings = gson.fromJson(offeringsString, Offerings.class);
        } catch (Exception e) {
            throw new JSONParseException(String.format("Parsing offerings file '%s': %s", offeringsPath, e.getMessage()));
        }

        try {
            System.out.println("- Reading Plan");
            String plansString = Files.readString(Path.of(planPath));
            plansList = gson.fromJson(plansString, PlansList.class);
        } catch (Exception e) {
            throw new JSONParseException(String.format("Parsing plans list file '%s': %s", planPath, e.getMessage()));
        }
    }

    private static void registerOutputTypeAdapters(GsonBuilder gb) {

    }

    private static void registerInputTypeAdapters(GsonBuilder gb) {
        // Misc
        gb.registerTypeAdapter(CourseID.class, new CourseID.Serializer());
        gb.registerTypeAdapter(CourseID.class, new CourseID.Deserializer());

        // Catalog
        gb.registerTypeAdapter(CatalogYear.class, new CatalogYear.Serializer());
        gb.registerTypeAdapter(CatalogYear.class, new CatalogYear.Deserializer());

        // Offerings
        gb.registerTypeAdapter(Offerings.class, new Offerings.Serializer());
        gb.registerTypeAdapter(Offerings.class, new Offerings.Deserializer());

        gb.registerTypeAdapter(SectionID.class, new SectionID.Serializer());
        gb.registerTypeAdapter(SectionID.class, new SectionID.Deserializer());

        gb.registerTypeAdapter(Time.class, new Time.Serializer());
        gb.registerTypeAdapter(Time.class, new Time.Deserializer());

        gb.registerTypeAdapter(Date.class, new Date.Serializer());
        gb.registerTypeAdapter(Date.class, new Date.Deserializer());

        // Plans
        gb.registerTypeAdapter(Plan.class, new Plan.Serializer());
        gb.registerTypeAdapter(Plan.class, new Plan.Deserializer());

        gb.registerTypeAdapter(PlanTerm.class, new PlanTerm.Serializer());
        gb.registerTypeAdapter(PlanTerm.class, new PlanTerm.Deserializer());
    }

}
