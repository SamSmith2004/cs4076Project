package ul.Server.Handlers;

import ul.Server.Utils.SessionData;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class RequestHandler {
    protected abstract String responseBuilder(SessionData sessionData) throws IOException;

    public static String jsonToString(JsonObject jsonObject) {
        Map<String, Object> responseConfig = new HashMap<>();
        // Enable pretty printing (Currently broken)
        // responseConfig.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(responseConfig);

        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = writerFactory.createWriter(stringWriter)) {
            jsonWriter.writeObject(jsonObject);
        }
        return stringWriter.toString();
    }

    public static String errorBuilder(Exception e) throws IOException {
        JsonObject errorResponse =
                Json.createObjectBuilder()
                        .add("status", "error")
                        .add("message", "Server error: " + e.getMessage())
                        .build();
        return jsonToString(errorResponse);
    }
}
