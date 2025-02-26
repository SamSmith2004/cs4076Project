package ul.Server.Handlers;

import ul.Server.Utils.DayOfWeek;
import ul.Server.Utils.Module;
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
            int id = content.getInt("id");
            String lecturer = content.getString("lecturer");
            String room = content.getString("room");
            String fromTime = content.getString("fromTime");
            String toTime = content.getString("toTime");
            Module module;
            DayOfWeek day;
            try {
                module = Module.valueOf(content.getString("module"));
                day = DayOfWeek.valueOf(content.getString("day"));
            } catch (IllegalArgumentException e) {
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "Invalid module or day")
                        .add("Content-Type", "addLecture")
                        .build();
            }

            // Validate time formats
            if (!fromTime.matches("\\d{1,2}:\\d{2}") || !toTime.matches("\\d{1,2}:\\d{2}")) {
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "Time must be in format HH:MM")
                        .add("Content-Type", "addLecture")
                        .build();
            }

            // Validate time range
            if (Integer.parseInt(fromTime.split(":")[0]) < 9 || Integer.parseInt(fromTime.split(":")[0]) > 17 ||
                    Integer.parseInt(toTime.split(":")[0]) < 9 || Integer.parseInt(toTime.split(":")[0]) > 17) {
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "Invalid time")
                        .add("Content-Type", "addLecture")
                        .build();
            }

            // For consistency, remove leading 0 from times
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
         int id = content.getInt("id");
         if (sessionData.removeLecture(id)) {
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
