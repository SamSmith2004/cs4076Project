package ul.cs4076project.Model;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.ArrayList;


public class ResponseHandler {
    private final JsonObject response;

    public ResponseHandler(JsonObject response) {
        this.response = response;
    }

    public Object extractResponse() {
        String contentType = response.getString("Content-Type");
        String status = response.getString("status");

        if (status.equals("InvalidActionException")) {
            return "Invalid Action Exception";
        }

        if (status.equals("error")) {
            return response.getString("content");
        }

        Object result = null;
        switch (contentType) {
            case "timetable" :
                result = buildTimetableResponse();
                break;
            case "addLecture", "test", "Message", "removeLecture":
                result = response.getString("content");
                break;
            default:
                System.out.println("Invalid Content-Type");
                break;
        }

        return result;
    }

    private Lecture[][] buildTimetableResponse() {
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
            JsonObject lectureJson = contentArray.getJsonObject(i);
            Lecture lecture = new Lecture(
                    lectureJson.getString("module"),
                    lectureJson.getString("lecturer"),
                    lectureJson.getString("room"),
                    lectureJson.getString("fromTime"),
                    lectureJson.getString("toTime"),
                    lectureJson.getString("day")
            );
            lectures.add(lecture);
        }

        // Create timetable and assign index based on day and time
        for (Lecture lecture : lectures) {
            int day = switch (lecture.getDay()) {
                case "Monday" -> 0;
                case "Tuesday" -> 1;
                case "Wednesday" -> 2;
                case "Thursday" -> 3;
                case "Friday" -> 4;
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

        return timetable;
    }
}

