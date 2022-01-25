package api.instance;

import api.SpecificationCache;
import api.SpecificationID;
import api.answer.EvaluationAnswer;
import exceptions.JSONParseException;
import exceptions.LinkingException;
import exceptions.PSLInstanceException;
import io.github.cdimascio.dotenv.Dotenv;
import preferences.specification.FullSpecification;
import psl.exceptions.PSLParsingError;
import spark.Response;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static spark.Spark.get;
import static spark.Spark.patch;

public class ServerInstance extends AppInstance {
    private SpecificationCache cache;
    HttpClient client;
    Dotenv dotenv;

    public ServerInstance() throws Exception {
        super();

        this.cache = new SpecificationCache();
        client = HttpClient.newHttpClient();
        dotenv = Dotenv.load();

        requestCatalog();
        linkCourses();
        requestAOS();
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        cache = new SpecificationCache();
        requestCatalog();
        requestAOS();
    }

    public void requestCatalog() throws PSLInstanceException, JSONParseException, LinkingException {
        String catalogJSON = requestResource(dotenv.get("CATALOG_QUERY"));
        loadCatalogString(catalogJSON);

        String offeringsJSON = requestResource(dotenv.get("SECTIONS_QUERY"));
        loadOfferingsString(offeringsJSON);
        linkCourses();
    }

    public void requestAOS() throws PSLInstanceException {
        String resource = requestResource(dotenv.get("AOS_QUERY"));
        addDependencyString(resource);
    }

    public FullSpecification requestPSL(SpecificationID id, boolean queryCache) throws PSLInstanceException {
        if (queryCache && cache.isCached(id)) {
            return cache.get(id);
        } else {
            String resource = requestSpecification(dotenv.get("PSL_QUERY"), id);
            FullSpecification specification = loadPSLString(resource);
            cache.add(id, specification);
            return specification;
        }
    }


    public FullSpecification requestPSLJson(SpecificationID id, boolean queryCache) throws PSLInstanceException {
        if (queryCache && cache.isCached(id)) {
            return cache.get(id);
        } else {
            String resource = requestSpecification(dotenv.get("PSL_JSON_QUERY"), id);
            FullSpecification specification = loadPSLJsonString(resource);
            cache.add(id, specification);
            return specification;
        }
    }

    private String requestResource(String resource) throws PSLInstanceException {
        String firebase = dotenv.get("FIREBASE");
        URI uri = URI.create(String.format("%s/%s", firebase, resource));
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new PSLInstanceException.PSLResourceException(request, response);
            } else {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new PSLInstanceException.PSLRequestFailureException(request, e);
        }
    }

    private String requestSpecification(String resource, SpecificationID id) throws PSLInstanceException {
        return requestResource(String.format(resource, id.identifier));
    }

    private static String handleError(Exception e, Response r) {
        if (e instanceof JSONParseException) {
            r.status(400);
        } else if (e instanceof PSLParsingError) {
            r.status(400);
        } else if (e instanceof PSLInstanceException.PSLResourceException) {
            r.status(404);
        } else if (e instanceof LinkingException) {
            r.status(424);
        } else {
            r.status(500);
        }
        e.printStackTrace();
        return e.getMessage();
    }

    public static void main(String[] args) throws Exception {
        ServerInstance instance = new ServerInstance();

        patch("/updateCatalog", (request, response) -> {
            try {
                instance.requestCatalog();
                return "success";
            } catch (Exception e) {
                return handleError(e, response);
            }
        });
        patch("/updateAOSCatalog", (request, response) -> {
            try {
                instance.requestAOS();
                return "success";
            } catch (Exception e) {
                return handleError(e, response);
            }
        });
        patch("/reset", (request, response) -> {
            try {
                instance.reset();
                return "success";
            } catch (Exception e) {
                return handleError(e, response);
            }
        });
        get("/evaluate/psl-json/:id/:version/:explain", (request, response) -> {
            try {
                SpecificationID id = new SpecificationID(request.params(":id"), request.params(":version"));
                FullSpecification specification = instance.requestPSL(id, true);
                String planJSON = request.body();
                boolean explain = request.params(":id").equals("explain");
                EvaluationAnswer answer = instance.evaluatePlansString(planJSON, specification, explain);
                return answer.toJSON();
            } catch (Exception e) {
                return handleError(e, response);
            }
        });
        get("/validate/psl-json/:id/:version", (request, response) -> {
            try {
                SpecificationID id = new SpecificationID(request.params(":id"), request.params(":version"));
                instance.requestPSLJson(id, false);
                return "success";
            } catch (Exception e) {
                return handleError(e, response);
            }
        });
        get("/validate/psl/:id/:version", (request, response) -> {
            try {
                SpecificationID id = new SpecificationID(request.params(":id"), request.params(":version"));
                instance.requestPSL(id, false);
                return "success";
            } catch (Exception e) {
                return handleError(e, response);
            }
        });
        get("/convert/psl-json/:id/:version", (request, response) -> {
            try {
                SpecificationID id = new SpecificationID(request.params(":id"), request.params(":version"));
                FullSpecification specification = instance.requestPSLJson(id, false);
//                return specification.toPSL();
                return "not implemented";
            } catch (Exception e) {
                return handleError(e, response);
            }
        });
        get("/convert/psl/:id/:version", (request, response) -> {
            try {
                SpecificationID id = new SpecificationID(request.params(":id"), request.params(":version"));
                FullSpecification specification = instance.requestPSL(id, false);
                return specification.toJSON();
            } catch (Exception e) {
                return handleError(e, response);
            }
        });
    }
}
