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

    public boolean removeLecture(int id) {
        for (Lecture lec : timeTable) {
            if (lec.getId() == id) {
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
                .add("id", lecture.getId())
                .add("module", lecture.getModuleString())
                .add("lecturer", lecture.getLecturer())
                .add("room", lecture.getRoom())
                .add("fromTime", lecture.getFromTime())
                .add("toTime", lecture.getToTime())
                .add("day", lecture.getDayString())
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
        timeTable.add(new Lecture(1, Module.CS4076, "Dr. Smith", "Room 101", "9:00", "10:00", DayOfWeek.MONDAY));
        timeTable.add(new Lecture(2, Module.CS4115, "Dr. Jones", "Room 102", "10:00", "11:00", DayOfWeek.TUESDAY));
        timeTable.add(new Lecture(3, Module.CS4006, "Dr. Brown", "Room 103", "11:00", "12:00", DayOfWeek.WEDNESDAY));
        timeTable.add(new Lecture(4, Module.CS4185, "Dr. Davis", "Room 104", "12:00", "13:00", DayOfWeek.THURSDAY));
        timeTable.add(new Lecture(5, Module.MA4413, "Dr. Wilson", "Room 105", "13:00", "14:00", DayOfWeek.FRIDAY));
    }
}