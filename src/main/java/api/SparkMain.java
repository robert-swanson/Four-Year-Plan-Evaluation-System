package api;

import exceptions.JSONParseException;
import exceptions.LinkingException;
import exceptions.PSLInstanceException;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import preferences.explanation.Explanation;
import psl.exceptions.PSLCompileError;

import static spark.Spark.*;

public class SparkMain {

    private static PSLInstance loadFromArgs(String[] args) throws Exception {
        String catalogPath = "", offeringsPath = "";

        LongOpt catalogOpt = new LongOpt("catalog", LongOpt.REQUIRED_ARGUMENT, null, 'c');
        LongOpt offeringsOpt = new LongOpt("offerings", LongOpt.REQUIRED_ARGUMENT, null, 'o');
        LongOpt[] longOpts = new LongOpt[]{catalogOpt, offeringsOpt};

        Getopt g = new Getopt("main", args, "", longOpts);
        while (g.getopt() != -1) {
            switch (g.getLongind()) {
                case 0 -> catalogPath = g.getOptarg();
                case 1 -> offeringsPath = g.getOptarg();
                default -> System.out.printf("Unrecognized option: '%s'\n", g.getOptopt());
            }
        }
        return new PSLInstance(catalogPath, offeringsPath);
    }

    public static void main(String[] args) throws Exception {
        PSLInstance instance;
        if (args.length > 0) {
            instance = loadFromArgs(args);
        } else {
            instance = new PSLInstance();
        }

        put("/initialize/catalog", (request, response) -> {
            try {
                instance.loadCatalogString(request.body());
                return "success";
            } catch (JSONParseException e) {
                response.status(400);
                return e.toString();
            } catch (Exception e) {
                response.status(500);
                return e.toString();
            }
        });
        put("/initialize/offerings", (request, response) -> {
            try {
                instance.loadOfferingsString(request.body());
                return "success";
            } catch (JSONParseException e) {
                response.status(400);
                return e.toString();
            } catch (Exception e) {
                response.status(500);
                return e.toString();
            }
        });
        put("/initialize/link", (request, response) -> {
            try {
                instance.linkCourses();
                return "success";
            } catch (PSLInstanceException e) {
                response.status(424);
                return e.toString();
            } catch (LinkingException e) {
                response.status(422);
                return e.toString();
            } catch (Exception e) {
                response.status(500);
                return e.toString();
            }
        });
        put("/configure/add-dependency", (request, response) -> {
            try {
                instance.addDependencyString(request.body());
                return "success";
            } catch (PSLCompileError e) {
                response.status(400);
                e.printStackTrace();
                return e.toString();
            } catch (Exception e) {
                response.status(500);
                e.printStackTrace();
                return e.toString();
            }
        });
        put("/configure/set-specification", (request, response) -> {
            try {
                instance.loadPSLString(request.body());
                return "success";
            } catch (Exception e) {
                response.status(500);
                e.printStackTrace();
                return e.toString();
            }
        });
        post("/evaluate-plans", (request, response) -> {
            try {
                Explanation explanation = instance.evaluatePlansString(request.body());
                return explanation.toJSON();
            } catch (Exception e) {
                response.status(500);
                e.printStackTrace();
                return e.toString();
            }
        });
        delete("/reset", (request, response) -> {
            try {
                instance.reset();
                return "success";
            } catch (Exception e) {
                response.status(500);
                e.printStackTrace();
                return e.toString();
            }
        });
    }
}