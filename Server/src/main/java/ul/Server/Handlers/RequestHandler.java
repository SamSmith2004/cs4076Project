package ul.Server.Handlers;

import jakarta.json.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public abstract class RequestHandler {
    /**
     * 
     * @return
     * @throws IOException
     */
    protected abstract String responseBuilder() throws IOException;

    /**
     * 
     * @param jsonObject
     * @return
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
     * 
     * @param e
     * @return
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
     * 
     * @param e
     * @return
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
