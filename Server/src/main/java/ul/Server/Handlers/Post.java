package ul.Server.Handlers;

import ul.Server.Server;
import ul.Server.Models.DayOfWeek;
import ul.Server.Models.Module;
import ul.Server.Models.Lecture;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.sql.SQLException;

/**
 * 
 */
public class Post extends RequestHandler {
    /**
     * 
     */
    JsonObject headers;
    /**
     * 
     */
    JsonObject content;

    /**
     * 
     * @param requestData
     */
    public Post(JsonObject requestData) {
        this.headers = requestData.getJsonObject("headers");
        this.content = requestData.getJsonObject("content");
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    @Override
    public String responseBuilder() throws IOException {
        JsonObject responseData;
        try {
            String contentType = headers.getString("Content-Type");

            responseData = switch (contentType) {
                case "addLecture" -> buildAddLectureResponse();
                case "removeLecture" -> buildRemoveLectureResponse();
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

    /**
     * 
     * @return
     */
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

    /**
     * 
     * @return
     */
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

    /**
     * 
     * @return
     * @see ul.Server.Models.Module
     * @see ul.Server.Models.DayOfWeek
     * @see ul.Server.Models.Lecture
     */
    private JsonObject buildAddLectureResponse() {
        try {
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

            // Check if timeslot overlaps with existing lectures
            try {
                if (Server.getDatabaseManager().lectureOverlaps(day, normalizedFromTime, normalizedToTime)) {
                    return Json.createObjectBuilder()
                            .add("status", "error")
                            .add("content", "Timeslot already taken")
                            .add("Content-Type", "addLecture")
                            .build();
                }
            } catch (SQLException e) {
                System.err.println("SQL Exception during overlap check: " + e.getMessage());
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "Database error")
                        .add("Content-Type", "addLecture")
                        .build();
            }

            Lecture newLecture = new Lecture(module, lecturer, room, normalizedFromTime, normalizedToTime, day);
            try {
                if (Server.getDatabaseManager().addLecture(newLecture)) {
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

    /**
     * 
     * @return
     */
    private JsonObject buildRemoveLectureResponse() {
        try {
            int id = content.getInt("id");

            try {
                if (Server.getDatabaseManager().removeLecture(id)) {
                    return Json.createObjectBuilder()
                            .add("status", "success")
                            .add("content", "Lecture removed")
                            .add("Content-Type", "removeLecture")
                            .build();
                } else {
                    return Json.createObjectBuilder()
                            .add("status", "error")
                            .add("content", "Lecture not found")
                            .add("Content-Type", "removeLecture")
                            .build();
                }
            } catch (SQLException e) {
                System.err.println("SQL Exception: " + e.getMessage());
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "Database error")
                        .add("Content-Type", "removeLecture")
                        .build();
            }
        } catch (JsonException e) {
            return serialError(e);
        }
    }
}
