package ul.cs4076projectserver.Handlers;

import ul.cs4076projectserver.Server;
import ul.cs4076projectserver.Models.DayOfWeek;
import ul.cs4076projectserver.Models.Module;
import ul.cs4076projectserver.Models.Lecture;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.sql.SQLException;

/**
 * The {@code Post} class handles POST requests to the server.
 * It processes the request data, extracts headers and content, and generates
 * appropriate responses based on the request.
 * 
 * <p>
 * This class extends {@link RequestHandler} and overrides the
 * {@link #responseBuilder()} method to build the response.
 * 
 * @see ul.cs4076projectserver.Handlers.RequestHandler
 * @see Lecture
 * @see DayOfWeek
 * @see Module
 */
public class Post extends RequestHandler {
    /**
     * A {@link JsonObject} containing the headers from the request data. These
     * headers are used to determine the type of response to generate.
     */
    JsonObject headers;
    /**
     * A {@link JsonObject} containing the content from the request data. This
     * content is used to process the specific POST request.
     */
    JsonObject content;

    /**
     * Constructs a {@code Post} request handler with the specified request data.
     * 
     * @param requestData The {@link JsonObject} containing the request data.
     */
    public Post(JsonObject requestData) {
        this.headers = requestData.getJsonObject("headers");
        this.content = requestData.getJsonObject("content");
    }

    /**
     * Builds the response for the POST request.
     * The response is generated based on the "Content-Type" header in the request
     * data.
     * 
     * @return A {@link String} representing the response.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String responseBuilder() throws IOException {
        JsonObject responseData;
        try {
            String contentType = headers.getString("Content-Type");

            responseData = switch (contentType) {
                case "addLecture" -> buildAddLectureResponse();
                case "removeLecture" -> buildRemoveLectureResponse();
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
     * Builds an invalid response indicating an error with the "Content-Type"
     * header.
     * 
     * @return A {@link JsonObject} representing the invalid response.
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
     * Builds the response for adding a lecture. The response is generated based on
     * the content of the request data.
     * 
     * @return A {@link JsonObject} representing the add lecture response.
     * @see Module
     * @see DayOfWeek
     * @see Lecture
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
     * Builds the response for removing a lecture. The response is generated based
     * on the content of the request data.
     * 
     * @return A {@link JsonObject} representing the remove lecture response.
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
