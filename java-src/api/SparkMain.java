package api;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import preferences.explanation.Explanation;
import psl.exceptions.PSLCompileError;

import static spark.Spark.*;

public class SparkMain {
    public static void main(String[] args) throws Exception {
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

        PSLInstance instance = new PSLInstance(catalogPath, offeringsPath);


//        put("/initialize/catalog", (request, response) -> {
//            try {
//                instance.loadCatalogString(request.body());
//                response.body("success");
//            } catch (JSONParseException e) {
//                response.status(400);
//                response.body(e.toString());
//            }
//            return response;
//        });
//        put("/initialize/offerings", (request, response) -> {
//            try {
//                instance.loadOfferingsString(request.body());
//                response.body("success");
//            } catch (JSONParseException e) {
//                response.status(400);
//                response.body(e.toString());
//            }
//            return response;
//        });
//        put("/initialize/link", (request, response) -> {
//            try {
//                instance.linkCourses();
//                response.body("success");
//            } catch (PSLInstanceException e) {
//                response.status(424);
//                response.body(e.toString());
//            } catch (LinkingException e) {
//                response.status(422);
//                response.body(e.toString());
//            }
//            return response;
//        });
        put("/configure/add-dependency", (request, response) -> {
            try {
                instance.addDependencyString(request.body());
                return "success";
            } catch (PSLCompileError e) {
                response.status(400);
                e.printStackTrace();
                return e.toString();
            }
        });
        put("/configure/set-specification", (request, response) -> {
            try {
                instance.loadPSLString(request.body());
                return "success";
            } catch (Exception e) {
                response.status(400);
                e.printStackTrace();
                return e.toString();
            }
        });
        get("/evaluate-plans", (request, response) -> {
            try {
                Explanation explanation = instance.evaluatePlansString(request.body());
                return explanation.toJSON();
            } catch (Exception e) {
                response.status(400);
                e.printStackTrace();
                return e.toString();
            }
        });
        delete("/reset", (request, response) -> {
            try {
                instance.reset();
                return "success";
            } catch (Exception e) {
                response.status(400);
                e.printStackTrace();
                return e.toString();
            }
        });
    }
}