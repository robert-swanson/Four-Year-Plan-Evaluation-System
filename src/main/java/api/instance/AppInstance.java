package api.instance;

import api.EvaluationAnswer;
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
import preferences.condition.Condition;
import preferences.constraints.Constraint;
import preferences.constraints.RequireableConstraint;
import preferences.context.Context;
import preferences.evaluators.ContextEvaluator;
import preferences.json.FullSpecificationDeserializer;
import preferences.json.PropertyBasedInterfaceMarshal;
import preferences.result.Result;
import preferences.scoring.ScoreFunction;
import preferences.specification.FullSpecification;
import preferences.specification.Specification;
import preferences.value.ScalableValue;
import preferences.value.Value;
import psl.PSLCompiler;
import psl.exceptions.PSLCompileError;

public abstract class AppInstance {
    private Catalog catalog;
    private Offerings offerings;
    private Link link;
    private PSLCompiler pslCompiler;
    private static Gson gson;

    public AppInstance() throws Exception {
        pslCompiler = new PSLCompiler();
        createGson();
    }

    public void reset() throws Exception {
        System.out.println("- Resetting PSL Instance");
        pslCompiler = new PSLCompiler();
    }

    protected void loadCatalogString(String catalogJson) throws JSONParseException {
        System.out.println("- Reading Catalog JSON");
        try {
            catalog = gson.fromJson(catalogJson, Catalog.class);
        } catch (Exception e) {
            throw new JSONParseException(String.format("Parsing catalog json: %s", e.getMessage()));
        }
    }

    protected void loadOfferingsString(String offeringsJson) throws JSONParseException {
        System.out.println("- Reading Course Offerings JSON");
        try {
            offerings =  gson.fromJson(offeringsJson, Offerings.class);
        } catch (Exception e) {
            throw new JSONParseException(String.format("Parsing offerings json: %s", e.getMessage()));
        }
    }

    protected void linkCourses() throws PSLInstanceException, LinkingException {
        System.out.println("- Linking Offerings to Courses");
        if (catalog == null) {
            throw new PSLInstanceException.PSLInstanceNotReadyException("needs a catalog");
        } else if (offerings == null) {
            throw new PSLInstanceException.PSLInstanceNotReadyException("needs a offerings");
        }
        link = new Link(catalog, offerings);
    }

    protected void addDependencyString(String dependencyPSL) throws PSLCompileError {
        System.out.println("- Compiling Dependency PSL string");
        pslCompiler.addDependencyPSLString(dependencyPSL);
    }

    public FullSpecification loadPSLString(String psl) throws PSLCompileError {
        System.out.println("- Compiling Main PSL String");
        return pslCompiler.compileString(psl);
    }

    protected FullSpecification loadPSLJsonString(String pslJSON) {
        System.out.println("- Compiling Main PSL JSON String");
        return new FullSpecification(pslJSON);
    }

    public EvaluationAnswer evaluatePlansString(String plansJson, Specification specification, boolean includeExplanation) throws Exception {
        if (link == null) {
            throw new PSLInstanceException.PSLInstanceNotReadyException("needs to be linked");
        } else if (specification == null) {
            throw new PSLInstanceException.PSLInstanceNotReadyException("needs a specification");
        }
        PlansList plansList = gson.fromJson(plansJson, PlansList.class);
        EvaluationAnswer answer = new EvaluationAnswer();

        for (Plan plan : plansList.plans) {
            long startTime = System.currentTimeMillis();

            plan.link(link);
            Context context = new Context(plan);
            specification.evaluate(context, includeExplanation);
            context.assertPlanContext();
            answer.addAnswer(plan.id, specification, includeExplanation);

            long endTime = System.currentTimeMillis();
            System.out.printf("- Plan '%s' scored %s (took %.4fs)\n", plan.id, specification.getLastResult().describe(), (endTime-startTime)/1000.0);
        }
        return answer;
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

        // PSL JSON
        gb.registerTypeAdapter(FullSpecification.class, new FullSpecificationDeserializer());

        PropertyBasedInterfaceMarshal marshal = new PropertyBasedInterfaceMarshal();
        Class[] classes = new Class[] {
                Specification.class, Constraint.class, RequireableConstraint.class, ContextEvaluator.class,
                Result.class, Value.class, ScalableValue.class, ScoreFunction.class, Condition.class
        };
        for (Class c: classes) {
            gb.registerTypeAdapter(c, marshal);
        }

        gson = gb.setPrettyPrinting().create();
        return gson;
    }
}
