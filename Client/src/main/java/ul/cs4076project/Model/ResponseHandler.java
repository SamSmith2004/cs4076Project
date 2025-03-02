package ul.cs4076project.Model;

import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import java.util.ArrayList;


public class ResponseHandler {
    private final JsonObject response;

    public ResponseHandler(JsonObject response) {
        this.response = response;
    }

    public ResponseType extractResponse() {
        try {
            String contentType = response.getString("Content-Type");
            String status = response.getString("status");

            if (status.equals("InvalidActionException")) {
                return new ResponseType.StringResponse("Invalid Action Exception");
            }

            if (status.equals("error")) {
                return new ResponseType.StringResponse(response.getString("content"));
            }

            ResponseType result = null;
            switch (contentType) {
                case "timetable" :
                    result = buildTimetableResponse();
                    break;
                case "addLecture", "test", "Message", "removeLecture":
                    result = new ResponseType.StringResponse(response.getString("content"));
                    break;
                default:
                    System.out.println("Invalid Content-Type");
                    break;
            }

            return result;
        } catch (JsonException e) {
            System.err.println("JSON error" + e.getMessage());
            return new ResponseType.StringResponse("Error occurred while processing response");
        } catch (NullPointerException e) {
            System.err.println("NullPointerException occurred" + e.getMessage());
            return new ResponseType.StringResponse("Error occurred while processing response");
        }
    }

    private ResponseType buildTimetableResponse() {
        JsonArray contentArray = response.getJsonArray("content");
        ArrayList<Lecture> lectures = new ArrayList<>();
        Lecture[][] timetable = new Lecture[5][9];

        // Initialize timetable with null values to prevent weird bugs
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                timetable[i][j] = null;
            }
        }

        for (int i = 0; i < contentArray.size(); i++) {
            try {
                JsonObject lectureJson = contentArray.getJsonObject(i);

                // Type conversion and validations
                String id = String.valueOf(lectureJson.getInt("id"));
                Module module;
                try {
                    module = Module.valueOf(lectureJson.getString("module"));
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid module: " + lectureJson.getString("module"));
                    continue; // Skip this lecture
                }
                DayOfWeek day;
                try {
                    day = DayOfWeek.valueOf(lectureJson.getString("day"));
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid day: " + lectureJson.getString("day"));
                    continue;
                }

                Lecture lecture = new Lecture(
                        id,
                        module,
                        lectureJson.getString("lecturer"),
                        lectureJson.getString("room"),
                        lectureJson.getString("fromTime"),
                        lectureJson.getString("toTime"),
                        day
                );
                lectures.add(lecture);
            } catch (JsonException | IllegalArgumentException e) {
                System.err.println("Error parsing lecture: " + e.getMessage());
            }
        }

        // Create timetable and assign index based on day and time
        for (Lecture lecture : lectures) {
            int day = switch (lecture.getDay()) {
                case MONDAY -> 0;
                case TUESDAY -> 1;
                case WEDNESDAY -> 2;
                case THURSDAY -> 3;
                case FRIDAY -> 4;
                default -> -1;
            };
            int time = switch (lecture.getFromTime()) {
                case "9:00" -> 0;
                case "10:00" -> 1;
                case "11:00" -> 2;
                case "12:00" -> 3;
                case "13:00" -> 4;
                case "14:00" -> 5;
                case "15:00" -> 6;
                case "16:00" -> 7;
                case "17:00" -> 8;
                default -> -1;
            };
            if (day >= 0 && time >= 0) {
                timetable[day][time] = lecture;
            }
        }

        return new ResponseType.TimetableResponse(timetable);
    }
}

