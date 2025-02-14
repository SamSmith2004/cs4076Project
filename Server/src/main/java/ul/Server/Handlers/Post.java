package ul.Server.Handlers;

import ul.Server.Utils.SessionData;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.Set;

public class Post extends RequestHandler {
    JsonObject headers;
    Set<String> headerKeys;
    JsonObject content;

    public Post(JsonObject requestData) {
        this.headerKeys = requestData.getJsonObject("headers").keySet();
        this.content = requestData.getJsonObject("content");
    }

    @Override
    public String responseBuilder(SessionData sessionData) throws IOException {
        try {
            for (String key : headerKeys) {
                switch (key) {
                    case "error":
                        throw new Exception("Error header present");
                    case "test":
                        System.out.println(headers.getString("test"));
                        break;
                    default:
                        break;
                }
            }

            String message = content.toString();

            // Build response
            JsonObject responseData =
                    Json.createObjectBuilder().add("status", "success").add("content", message).build();

            return jsonToString(responseData);

        } catch (JsonException e) {
            System.err.println("JsonException occurred: " + e.getMessage());
            return errorBuilder(e);
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            return errorBuilder(e);
        }
    }
}
