package ul.cs4076projectserver.Handlers;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import ul.cs4076projectserver.Models.DayOfWeek;
import ul.cs4076projectserver.Models.Lecture;
import ul.cs4076projectserver.Models.Module;
import ul.cs4076projectserver.Server;

import java.io.IOException;
import java.sql.SQLException;

public class Update extends RequestHandler {
    JsonObject headers;
    JsonObject content;

    public Update(JsonObject requestData) {
        this.headers = requestData.getJsonObject("headers");
        this.content = requestData.getJsonObject("content");
    }

    public String responseBuilder() throws IOException {
        JsonObject responseData;
        try {
            String contentType = headers.getString("Content-Type");

            responseData = (contentType.equals("replaceLecture")) ? buildUpdateLectureResponse() : buildInvalidResponse();

            return jsonToString(responseData);

        } catch (JsonException e) {
            System.err.println("JsonException occurred: " + e.getMessage());
            return errorBuilder(e);
        } catch (NullPointerException e) {
            System.err.println("NullPointerException occurred: " + e.getMessage());
            return errorBuilder(e);
        }
    }

    private JsonObject buildUpdateLectureResponse() {
        try {
            String id = content.getString("id");
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

            // Validate nothing is empty (try-catch handles module and day)
            if (lecturer.isEmpty() || room.isEmpty() || fromTime.isEmpty() || toTime.isEmpty()) {
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "All fields must be filled")
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
                    Integer.parseInt(toTime.split(":")[0]) < 9 || Integer.parseInt(toTime.split(":")[0]) > 18) {
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "Invalid time")
                        .add("Content-Type", "addLecture")
                        .build();
            }

            // Validate lecturer and room length
            if (lecturer.length() > 100) {
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "Lecturer name too long (max 100 characters)")
                        .add("Content-Type", "addLecture")
                        .build();
            }
            if (room.length() > 50) {
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "Room name too long (max 50 characters)")
                        .add("Content-Type", "addLecture")
                        .build();
            }

            // For consistency, remove leading 0 from times
            String normalizedFromTime = fromTime.replaceFirst("^0", "").trim();
            String normalizedToTime = toTime.replaceFirst("^0", "").trim();

            Lecture newLecture = new Lecture(id, module, lecturer, room, normalizedFromTime, normalizedToTime, day);
            try {
                if (Server.getDatabaseManager().updateLecture(newLecture)) {
                    return Json.createObjectBuilder()
                            .add("status", "success")
                            .add("content", "Lecture added")
                            .add("Content-Type", "addLecture")
                            .build();
                } else {
                    return Json.createObjectBuilder()
                            .add("status", "error")
                            .add("content", "Failed to add lecture to database")
                            .add("Content-Type", "addLecture")
                            .build();
                }
            } catch (SQLException e) {
                System.err.println("SQL Exception: " + e.getMessage());
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "Database error")
                        .add("Content-Type", "addLecture")
                        .build();
            }
        } catch (JsonException e) {
            return serialError(e);
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
}
