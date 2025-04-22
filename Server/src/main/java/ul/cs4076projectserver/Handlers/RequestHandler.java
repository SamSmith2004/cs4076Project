package ul.cs4076projectserver.Handlers;

import jakarta.json.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class RequestHandler {
    public RequestHandler() {
    }

    protected abstract String responseBuilder() throws IOException;

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

    public static String errorBuilder(Exception e) {
        System.err.println("Error: " + e.getMessage());
        JsonObject errorResponse = Json.createObjectBuilder()
                .add("status", "error")
                .add("content", "An unexpected error occurred.")
                .add("Content-Type", "error")
                .build();
        return jsonToString(errorResponse);
    }

    protected JsonObject serialError(Exception e) {
        System.err.println("Serial error: " + e.getMessage());
        return Json.createObjectBuilder()
                .add("status", "error")
                .add("content", "An error occurred while building the response.")
                .add("Content-Type", "error")
                .build();
    }
}
