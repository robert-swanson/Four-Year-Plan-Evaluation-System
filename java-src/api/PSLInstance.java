package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.JSONParseException;
import exceptions.LinkingException;
import exceptions.PSLInstanceException;
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
import psl.exceptions.PSLCompileError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PSLInstance {
    private Catalog catalog;
    private String catalogFile;
    private Offerings offerings;
    private String offeringsFile;
    private Link link;

    private FullSpecification specification;
    private PSLCompiler pslCompiler;

    private static Gson gson;

    public PSLInstance(String catalogFile, String offeringsFile) throws Exception {
        this.catalogFile = catalogFile;
        this.offeringsFile = offeringsFile;

        createGson();
        pslCompiler = new PSLCompiler();
        loadCatalogFile(catalogFile);
        loadOfferingsFile(offeringsFile);
        linkCourses();
    }

    public void reset() throws Exception {
        System.out.println("- Resetting PSL Instance");
        if (offeringsFile == null || catalogFile == null) {
            throw new PSLInstanceException("cannot reset unless catalog and offerings were loaded as file");
        }
        pslCompiler = new PSLCompiler();
        loadCatalogFile(catalogFile);
        loadOfferingsFile(offeringsFile);
        linkCourses();
    }

    public PSLInstance() {
        createGson();
        pslCompiler = new PSLCompiler();
    }

    public void loadCatalogString(String catalogJson) throws JSONParseException {
        System.out.println("- Reading Catalog JSON");
        try {
            catalog = gson.fromJson(catalogJson, Catalog.class);
        } catch (Exception e) {
            throw new JSONParseException(String.format("Parsing catalog json: %s", e.getMessage()));
        }
    }

    public void loadCatalogFile(String catalogFile) throws JSONParseException {
        System.out.printf("- Reading Catalog File: %s\n", catalogFile);
        try {
            String catalogString = Files.readString(Path.of(catalogFile));
            loadCatalogString(catalogString);
        } catch (IOException e) {
            throw new JSONParseException(String.format("Loading catalog file '%s': %s", catalogFile, e.getMessage()));
        }
    }

    public void loadOfferingsString(String offeringsJson) throws JSONParseException {
        System.out.println("- Reading Course Offerings JSON");
        try {
            offerings =  gson.fromJson(offeringsJson, Offerings.class);
        } catch (Exception e) {
            throw new JSONParseException(String.format("Parsing offerings json: %s", e.getMessage()));
        }
    }

    public void loadOfferingsFile(String offeringsFile) throws JSONParseException {
        System.out.printf("- Reading Course Offerings: %s\n", offeringsFile);
        try {
            String offeringsString = Files.readString(Path.of(offeringsFile)) ;
            loadOfferingsString(offeringsString);
        } catch (Exception e) {
            throw new JSONParseException(String.format("Parsing offerings file '%s': %s", offeringsFile, e.getMessage()));
        }
    }

    public void linkCourses() throws PSLInstanceException, LinkingException {
        System.out.println("- Linking Offerings to Courses");
        if (catalog == null) {
            throw new PSLInstanceException.PSLInstanceNotReadyException("needs a catalog");
        } else if (offerings == null) {
            throw new PSLInstanceException.PSLInstanceNotReadyException("needs a offerings");
        }
        link = new Link(catalog, offerings);
    }

    public void addDependencyString(String dependencyPSL) throws PSLCompileError {
        System.out.println("- Compiling Dependency PSL string");
        pslCompiler.addDependencyPSLString(dependencyPSL);
    }

    public void addDependencyFile(String dependencyFile) throws PSLCompileError {
        System.out.printf("- Compiling Dependency PSL: %s\n", dependencyFile);
        pslCompiler.addDependencyFile(dependencyFile);
    }

    public void loadPSLString(String psl) throws PSLCompileError {
        System.out.println("- Compiling Main PSL String");
        try {
            specification = pslCompiler.compileString(psl);
        } catch (IOException e) {
            throw new PSLCompileError("Parsing String", null);
        }
    }
    public void loadPSLFile(String pslFile) throws PSLCompileError {
        System.out.printf("- Compiling Main PSL: %s\n", pslFile);
        specification = pslCompiler.compileFile(pslFile);
    }

    public Explanation evaluatePlanFile(String planFile) throws Exception {
        System.out.printf("- Evaluating Plan: %s\n", planFile);
        try {
            String plansString = Files.readString(Path.of(planFile));
            return evaluatePlansString(plansString);
        } catch (Exception e) {
            throw new JSONParseException(String.format("Parsing plans list file '%s': %s", planFile, e.getMessage()));
        }
    }

    public Explanation evaluatePlansString(String plansJson) throws Exception {
        if (link == null) {
            throw new PSLInstanceException.PSLInstanceNotReadyException("needs to be linked");
        } else if (specification == null) {
            throw new PSLInstanceException.PSLInstanceNotReadyException("needs a specification");
        }
        PlansList plansList = gson.fromJson(plansJson, PlansList.class);
        plansList.link(link);
        Context context = new Context(plansList.getPlan(0));

        long startTime = System.currentTimeMillis();
        specification.evaluate(context, true);
        long endTime = System.currentTimeMillis();

        context.assertPlanContext();
        System.out.printf("- Plan is %s (took %.4fs)\n", specification.getLastResult().describe(), (endTime-startTime)/1000.0);
        return specification.explainLastResult();
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
