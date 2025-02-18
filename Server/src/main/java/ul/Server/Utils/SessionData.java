package ul.Server.Utils;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.ArrayList;

public class SessionData {
    ArrayList<Lecture> timeTable =  new ArrayList<>();

    public SessionData() {}

    public void addLecture(Lecture lecture) {
        timeTable.add(lecture);
    }

    public ArrayList<Lecture> getTimeTable() {
        return timeTable;
    }

    public void clearTimeTable() {
        timeTable.clear();
    }

    public boolean removeLecture(String day, String fromTime, String toTime) {
        for (Lecture lec : timeTable) {
            if (lec.getDay().equals(day) && normalisedTime(lec.getFromTime()).equals(fromTime) && normalisedTime(lec.getToTime()).equals(toTime)) {
                timeTable.remove(lec);
                return true;
            }
        }
        return false;
    }

    private String normalisedTime(String time) {
        return time.replaceFirst("^0", "");
    }

    private JsonObject serializeLecture(Lecture lecture) {
        return Json.createObjectBuilder()
                .add("module", lecture.getModule())
                .add("lecturer", lecture.getLecturer())
                .add("room", lecture.getRoom())
                .add("fromTime", lecture.getFromTime())
                .add("toTime", lecture.getToTime())
                .add("day", lecture.getDay())
                .build();
    }

    public void replaceLecture(int index, Lecture lecture) {
        timeTable.set(index, lecture);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Lecture lecture : timeTable) {
            sb.append(lecture.toString());
            sb.append("\n");
        }
        return sb.toString();
    }


    public void fillMockData() {
        timeTable.add(new Lecture("CS101", "Dr. Smith", "Room 101", "9:00", "10:00", "Monday"));
        timeTable.add(new Lecture("CS102", "Dr. Smith", "Room 102", "10:00","11:00", "Tuesday"));
        timeTable.add(new Lecture("CS103", "Dr. Smith", "Room 103", "11:00","12:00", "Wednesday"));
        timeTable.add(new Lecture("CS104", "Dr. Smith", "Room 104", "12:00","13:00", "Thursday"));
        timeTable.add(new Lecture("CS105", "Dr. Smith", "Room 105", "13:00", "14:00","Friday"));
    }
}