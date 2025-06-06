package ul.cs4076projectserver.Handlers;

import ul.cs4076projectserver.Server;
import ul.cs4076projectserver.Models.Lecture;

import jakarta.json.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

public class Get extends RequestHandler {
    JsonObject headers;
    Set<String> headerKeys;

    public Get(JsonObject requestData) {
        this.headers = requestData.getJsonObject("headers");
        this.headerKeys = headers.keySet();
    }

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

    private JsonObject buildTimetableResponse() {
        try {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

            try {
                ArrayList<Lecture> lectures = Server.getDatabaseManager().getLectures();
                try {
                    Server.setLectures(lectures);
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                }

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
