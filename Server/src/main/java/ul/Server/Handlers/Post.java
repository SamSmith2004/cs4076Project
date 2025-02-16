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
                case "test" -> buildTestResponse();
                default -> buildInvalidResponse();
            };

            return jsonToString(responseData);

        } catch (JsonException e) {
            System.err.println("JsonException occurred: " + e.getMessage());
            return errorBuilder(e);
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            return errorBuilder(e);
        }
    }

    private JsonObject buildInvalidResponse() {
        return Json.createObjectBuilder()
                .add("status", "error")
                .add("content", "Invalid Content-Type")
                .add("Content-Type", "Error")
                .build();
    }

    private JsonObject buildTestResponse() {
        return Json.createObjectBuilder()
                .add("status", "success")
                .add("content", "Test response")
                .add("Content-Type", "test")
                .build();
    }

    private JsonObject buildAddLectureResponse(SessionData sessionData) {
        String module = content.getString("module");
        String lecturer = content.getString("lecturer");
        String room = content.getString("room");
        String time = content.getString("time");
        String day = content.getString("day");

        // Normalize time to prevent comparison failures
        String normalizedTime = time.replaceFirst("^0", "");

        // Check if timeslot is already taken
        ArrayList<Lecture> lectures = sessionData.getTimeTable();
        for (Lecture lecture : lectures) {
            String existingTime = lecture.getTime().replaceFirst("^0", "");
            if (existingTime.equals(normalizedTime) && lecture.getDay().equals(day)) {
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "Timeslot already taken")
                        .add("Content-Type", "addLecture")
                        .build();
            }
        }

        Lecture lecture = new Lecture(module, lecturer, room, time, day);
        sessionData.addLecture(lecture);

        return Json.createObjectBuilder()
                .add("status", "success")
                .add("content", "Lecture added")
                .add("Content-Type", "addLecture")
                .build();
    }
}
