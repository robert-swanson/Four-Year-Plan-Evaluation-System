package exceptions;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PSLInstanceException extends Exception {
    public PSLInstanceException(String message) {
        super(message);
    }

    public static class PSLInstanceNotReadyException extends PSLInstanceException {
        public PSLInstanceNotReadyException(String reason) {
            super(String.format("PSL Instance %s", reason));
        }
    }

    public static class PSLResourceException extends PSLInstanceException {
        public PSLResourceException(HttpRequest request, HttpResponse<String> response) {
            super(String.format("Request for '%s' failed with status code %d: %s", request.uri(), response.statusCode(), response.body()));
        }
    }

    public static class PSLRequestFailureException extends PSLInstanceException {
        public PSLRequestFailureException(HttpRequest request, Exception e) {
            super(String.format("Request for '%s' failed because of an error: %s", request.uri(), e.getMessage()));
        }
    }
}
