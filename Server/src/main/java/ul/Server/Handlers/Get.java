package ul.Server.Handlers;

import ul.Server.Utils.Lecture;
import ul.Server.Utils.SessionData;

import javax.json.*;
import java.io.IOException;
import java.util.Set;

public class Get extends RequestHandler {
    JsonObject headers;
    Set<String> headerKeys;

    public Get(JsonObject requestData) {
        this.headers = requestData.getJsonObject("headers");
        this.headerKeys = headers.keySet();
    }

    @Override
    public String responseBuilder(SessionData sessionData) throws IOException {
        JsonObject responseData = null;
        try {
            for (String key : headerKeys) {
                // "Enhanced" switch statement to make IDE warnings stop
                responseData = switch (key) {
                    case "error" -> throw new Exception("Error header present");
                    case "getTimetable" -> {
                        yield buildTimetableResponse(sessionData);
                    }
                    default -> {
                        System.out.println("GET requests require a URL header");
                        yield buildInvalidUrlResponse();
                    }
                };
            }

            return jsonToString(responseData);

        } catch (JsonException e) {
            System.err.println("JsonException occurred: " + e.getMessage());
            return errorBuilder(e);
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            return errorBuilder(e);
        }
    }

    private JsonObject buildInvalidUrlResponse() {
        return Json.createObjectBuilder()
                .add("status", "error")
                .add("message", "Invalid URL")
                .build();
    }

    private JsonObject buildTimetableResponse(SessionData sessionData) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (Lecture lecture : sessionData.getTimeTable()) {
            JsonObjectBuilder lectureBuilder = Json.createObjectBuilder()
                    .add("module", lecture.getModule())
                    .add("lecturer", lecture.getLecturer())
                    .add("room", lecture.getRoom())
                    .add("time", lecture.getTime())
                    .add("day", lecture.getDay());
            arrayBuilder.add(lectureBuilder);
        }

        return Json.createObjectBuilder().add("status", "success").add("content", arrayBuilder).build();
    }
}
