package ul.Server.Handlers;

import ul.Server.Utils.Lecture;
import ul.Server.Utils.SessionData;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;

public class Post extends RequestHandler {
    JsonObject headers;
    JsonObject content;

    public Post(JsonObject requestData) {
        this.headers = requestData.getJsonObject("headers");
        this.content = requestData.getJsonObject("content");
    }

    @Override
    public String responseBuilder(SessionData sessionData) throws IOException {
        JsonObject responseData;
        try {
            String contentType = headers.getString("Content-Type");

            responseData = switch (contentType) {
                case "addLecture" -> buildAddLectureResponse(sessionData);
                case "removeLecture" -> buildRemoveLectureResponse(sessionData);
                case "test" -> buildTestResponse();
                default -> buildInvalidResponse();
            };

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

    private JsonObject buildTestResponse() {
        try {
            return Json.createObjectBuilder()
                    .add("status", "success")
                    .add("content", "Test response")
                    .add("Content-Type", "test")
                    .build();
        } catch (JsonException e) {
            return serialError(e);
        }
    }

    private JsonObject buildAddLectureResponse(SessionData sessionData) {
        try {
            String module = content.getString("module");
            String lecturer = content.getString("lecturer");
            String room = content.getString("room");
            String fromTime = content.getString("fromTime");
            String toTime = content.getString("toTime");
            String day = content.getString("day");

            // Normalize time to prevent comparison failures
            String normalizedFromTime = fromTime.replaceFirst("^0", "");
            String normalizedToTime = toTime.replaceFirst("^0", "");

            Lecture newLecture = new Lecture(module, lecturer, room, normalizedFromTime, normalizedToTime, day);

            // Check if timeslot overlaps with existing lectures
            ArrayList<Lecture> lectures = sessionData.getTimeTable();
            for (Lecture existingLecture : lectures) {
                if (newLecture.overlaps(existingLecture)) {
                    return Json.createObjectBuilder()
                            .add("status", "error")
                            .add("content", "Timeslot already taken")
                            .add("Content-Type", "addLecture")
                            .build();
                }
            }

            sessionData.addLecture(newLecture);
            return Json.createObjectBuilder()
                    .add("status", "success")
                    .add("content", "Lecture added")
                    .add("Content-Type", "addLecture")
                    .build();
        } catch (JsonException e) {
            return serialError(e);
        }
    }

    private JsonObject buildRemoveLectureResponse(SessionData sessionData) {
        String fromTime = content.getString("fromTime");
        String day = content.getString("day");

        // Normalize time to prevent comparison failures
        String normalizedFromTime = fromTime.replaceFirst("^0", "");

         if (sessionData.removeLecture(day, normalizedFromTime)) {
             try {
                 return Json.createObjectBuilder()
                         .add("status", "success")
                         .add("content", "Lecture removed")
                         .add("Content-Type", "removeLecture")
                         .build();
             } catch (JsonException e) {
                 return serialError(e);
             }
         } else {
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "Lecture not found")
                        .add("Content-Type", "removeLecture")
                        .build();
         }
    }
}
