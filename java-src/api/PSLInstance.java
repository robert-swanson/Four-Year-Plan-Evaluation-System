package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.JSONParseException;
import objects.Link;
import objects.catalog.Catalog;
import objects.misc.*;
import objects.offerings.Offerings;
import objects.plan.Plan;
import objects.plan.PlanTerm;
import objects.plan.PlansList;
import preferences.context.Context;
import preferences.explanation.Explanation;
import preferences.specification.FullSpecification;
import psl.PSLCompiler;

import java.nio.file.Files;
import java.nio.file.Path;

public class PSLInstance {
    private Catalog catalog;
    private Offerings offerings;
    private Link link;

    private FullSpecification specification;
    private PSLCompiler pslCompiler;

    private static Gson gson;

    public PSLInstance(String catalogFile, String offeringsFile) throws Exception {
        createGson();
        catalog = loadCatalogFile(catalogFile);
        offerings = loadOfferingsFile(offeringsFile);
        link = new Link(catalog, offerings);
        pslCompiler = new PSLCompiler();
    }

    public void addDependencyFile(String dependencyFile) {
        System.out.printf("- Compiling Dependency PSL: %s\n", dependencyFile);
        pslCompiler.addDependency(dependencyFile);
    }

    public void loadPSLFile(String pslFile) {
        System.out.printf("- Compiling Main PSL: %s\n", pslFile);
        specification = pslCompiler.compile(pslFile);
    }

    public Explanation evaluatePlanFile(String planFile) throws Exception {
        System.out.printf("- Evaluating Plan: %s\n", planFile);
        PlansList plansList = loadPlansFile(planFile);
        plansList.link(link);
        Context context = new Context(plansList.getPlan(0));

        long startTime = System.currentTimeMillis();
        specification.evaluate(context, true);
        long endTime = System.currentTimeMillis();

        context.assertPlanContext();
        System.out.printf("- Plan is %s (took %.4fs)\n", specification.getLastResult().describe(), (endTime-startTime)/1000.0);
        return specification.explainLastResult();
    }

    private static Catalog loadCatalogFile(String catalogFile) throws JSONParseException {
        try {
            System.out.printf("- Reading Catalog File: %s\n", catalogFile);
            String catalogString = Files.readString(Path.of(catalogFile));
            return gson.fromJson(catalogString, Catalog.class);
        } catch (Exception e) {
            throw new JSONParseException(String.format("Parsing catalog file '%s': %s", catalogFile, e.getMessage()));
        }
    }

    private static Offerings loadOfferingsFile(String offeringsFile) throws JSONParseException {
        try {
            System.out.printf("- Reading Course Offerings: %s\n", offeringsFile);
            String offeringsString = Files.readString(Path.of(offeringsFile)) ;
            return gson.fromJson(offeringsString, Offerings.class);
        } catch (Exception e) {
            throw new JSONParseException(String.format("Parsing offerings file '%s': %s", offeringsFile, e.getMessage()));
        }
    }


    private static PlansList loadPlansFile(String planFile) throws JSONParseException {
        try {
            String plansString = Files.readString(Path.of(planFile));
            return gson.fromJson(plansString, PlansList.class);
        } catch (Exception e) {
            throw new JSONParseException(String.format("Parsing plans list file '%s': %s", planFile, e.getMessage()));
        }
    }

    public static Gson createGson() {
        if (gson != null) return gson;
        GsonBuilder gb = new GsonBuilder();

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

        gson = gb.setPrettyPrinting().create();
        return gson;
    }
}