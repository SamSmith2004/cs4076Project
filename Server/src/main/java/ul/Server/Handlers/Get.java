package ul.Server.Handlers;

import ul.Server.Server;
import ul.Server.Models.Lecture;

import jakarta.json.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

/**
 * The {@code Get} class handles GET requests to the server.
 * It processes the request data, extracts headers, and generates appropriate
 * responses based on the request.
 * 
 * <p>
 * This class extends {@link RequestHandler} and overrides the
 * {@link #responseBuilder()} method to build the response.
 * 
 * @see ul.Server.Handlers.RequestHandler
 * @see ul.Server.Models.Lecture
 */
public class Get extends RequestHandler {
    /**
     * A {@link JsonObject} containing the headers from the request data.
     * These headers are used to determine the type of response to generate.
     */
    JsonObject headers;
    /**
     * A {@link Set} of {@link String} keys representing the headers from the
     * request data. These keys are used to access specific header values in the
     * {@link JsonObject} headers.
     */
    Set<String> headerKeys;

    /**
     * Constructs a {@code Get} request handler with the specified request data.
     * 
     * @param requestData The {@link JsonObject} containing the request data.
     */
    public Get(JsonObject requestData) {
        this.headers = requestData.getJsonObject("headers");
        this.headerKeys = headers.keySet();
    }

    /**
     * Builds the response for the GET request. The response is generated based on
     * the "Content-Type" header in the request data.
     * 
     * @return A {@link String} representing the resonse.
     */
    @Override
    public String responseBuilder() {
        JsonObject responseData;
        try {
            String contentType = headers.getString("Content-Type");

            responseData = (contentType.equals("timetable")) ? buildTimetableResponse() : buildInvalidResponse();
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
     * header. It does this by concatenating strings into a {@link JsonObject}.
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
     * Builds the response containing the timetable data. The timetable data is
     * retrieved from the database and foramtted as a JSON array.
     * 
     * @return A {@link JsonObject} representing the timetable response
     * @see ul.Server.Models.Lecture
     */
    private JsonObject buildTimetableResponse() {
        try {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

            try {
                ArrayList<Lecture> lectures = Server.getDatabaseManager().getLectures();

                for (Lecture lecture : lectures) {
                    JsonObjectBuilder lectureBuilder = Json.createObjectBuilder()
                            .add("id", lecture.getId())
                            .add("module", lecture.getModuleString())
                            .add("lecturer", lecture.getLecturer())
                            .add("room", lecture.getRoom())
                            .add("fromTime", lecture.getFromTime())
                            .add("toTime", lecture.getToTime())
                            .add("day", lecture.getDayString());
                    arrayBuilder.add(lectureBuilder);
                }

                return Json.createObjectBuilder()
                        .add("status", "success")
                        .add("Content-Type", "timetable")
                        .add("content", arrayBuilder)
                        .build();

            } catch (SQLException e) {
                System.err.println("SQL Exception: " + e.getMessage());
                return Json.createObjectBuilder()
                        .add("status", "error")
                        .add("content", "Database error: " + e.getMessage())
                        .add("Content-Type", "error")
                        .build();
            }
        } catch (JsonException | ArrayStoreException e) {
            return serialError(e);
        }
    }
}
