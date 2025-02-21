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
    public String responseBuilder(SessionData sessionData) {
        JsonObject responseData;
        try {
            String contentType = headers.getString("Content-Type");

            responseData = (contentType.equals("timetable")) ? buildTimetableResponse(sessionData) : buildInvalidResponse();
            return jsonToString(responseData);

        } catch (JsonException e) {
            System.err.println("JsonException occurred: " + e.getMessage());
            return errorBuilder(e);
        } catch (NullPointerException e) {
            System.err.println("NullPointerException occurred: " + e.getMessage());
            return errorBuilder(e);
        }
    }

    private JsonObject buildInvalidResponse() {
        try {
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("content", "Invalid Content-Type")
                    .add("Content-Type", "Error")
                    .build();
        } catch (JsonException e) {
            return serialError(e);
        }
    }

    private JsonObject buildTimetableResponse(SessionData sessionData) {
        try {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

            for (Lecture lecture : sessionData.getTimeTable()) {
                JsonObjectBuilder lectureBuilder = Json.createObjectBuilder()
                        .add("module", lecture.getModule())
                        .add("lecturer", lecture.getLecturer())
                        .add("room", lecture.getRoom())
                        .add("fromTime", lecture.getFromTime())
                        .add("toTime", lecture.getToTime())
                        .add("day", lecture.getDay());
                arrayBuilder.add(lectureBuilder);
            }

            return Json.createObjectBuilder().add("status", "success").add("Content-Type", "timetable").add("content", arrayBuilder).build();
        } catch (JsonException | ArrayStoreException e) {
            return serialError(e);
        }
    }
}
