package ul.Server.Handlers;

import jakarta.json.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code RequestHandler} class is an abstract class for handling server
 * requests. It provides methods for building responses and handling erros in a
 * consistent manner.
 */
public abstract class RequestHandler {
    /**
     * Default arg-less {@link RequestHandler} constructor - not used.
     */
    public RequestHandler() {
    }

    /**
     * Builds the response for the request. This method must be implemented by the
     * subclass it's called by to generate an appropriate response.
     * 
     * @return A {@link String} representing the response.
     * @throws IOException If an I/O error occurs.
     */
    protected abstract String responseBuilder() throws IOException;

    /**
     * Converts a {@link JsonObject} to it's {@link String} representation.
     * 
     * @param jsonObject The {@link JsonObject} to convert.
     * @return A {@link String} representing the Json objct.
     */
    public static String jsonToString(JsonObject jsonObject) {
        try {
            Map<String, JsonObject> responseConfig = new HashMap<>();
            JsonWriterFactory writerFactory = Json.createWriterFactory(responseConfig);

            StringWriter stringWriter = new StringWriter();
            try (JsonWriter jsonWriter = writerFactory.createWriter(stringWriter)) {
                jsonWriter.writeObject(jsonObject);
            }
            return stringWriter.toString();
        } catch (JsonException e) {
            System.err.println("JsonException occurred: " + e.getMessage());
            return errorBuilder(e);
        }
    }

    /**
     * Builds an error response for the given exception. The exception is formatted
     * in a specific
     * 
     * @param e The exception that occurred.
     * @return A {@link String} representing the error response.
     */
    public static String errorBuilder(Exception e) {
        System.err.println("Error: " + e.getMessage());
        JsonObject errorResponse = Json.createObjectBuilder()
                .add("status", "error")
                .add("content", "An unexpected error occurred.")
                .add("Content-Type", "error")
                .build();
        return jsonToString(errorResponse);
    }

    /**
     * Builds an error response for the given exception.
     * 
     * @param e The exception that occurred.
     * @return A {@link JsonObject} representing the error response.
     */
    protected JsonObject serialError(Exception e) {
        System.err.println("Serial error: " + e.getMessage());
        return Json.createObjectBuilder()
                .add("status", "error")
                .add("content", "An error occurred while building the response.")
                .add("Content-Type", "error")
                .build();
    }
}
