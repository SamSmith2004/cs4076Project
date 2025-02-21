package ul.Server.Handlers;

import ul.Server.Utils.SessionData;

import javax.json.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class RequestHandler {
    protected abstract String responseBuilder(SessionData sessionData) throws IOException;

    public static String jsonToString(JsonObject jsonObject) {
        try {
            Map<String, JsonObject> responseConfig = new HashMap<>();
            // Enable pretty printing (Currently broken)
            // responseConfig.put(JsonGenerator.PRETTY_PRINTING, true);
            JsonWriterFactory writerFactory = Json.createWriterFactory(responseConfig);

            StringWriter stringWriter = new StringWriter();
            try (JsonWriter jsonWriter = writerFactory.createWriter(stringWriter)) {
                jsonWriter.writeObject(jsonObject);
            }
            return stringWriter.toString();
        } catch (JsonException e) {
            System.err.println("JsonException occurred: " + e.getMessage());
            return errorBuilder(e);
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            return errorBuilder(e);
        }
    }

    public static String errorBuilder(Exception e) {
        JsonObject errorResponse =
                Json.createObjectBuilder()
                        .add("status", "error")
                        .add("message", "Server error: " + e.getMessage())
                        .add("Content-Type", "Error")
                        .build();
        return jsonToString(errorResponse);
    }

    protected JsonObject serialError (Exception e) {
        return Json.createObjectBuilder()
                .add("status", "error")
                .add("content", e.getMessage())
                .add("Content-Type", "error")
                .build();
    }
}
