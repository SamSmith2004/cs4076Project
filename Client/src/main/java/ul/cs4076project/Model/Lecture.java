package ul.cs4076project.Model;

import javax.json.Json;
import javax.json.JsonObject;

public class Lecture {
    private String module;
    private String lecturer;
    private String room;
    private String fromTime;
    private String toTime;
    private String day;

    public Lecture(String mod, String lec, String rm, String from, String to, String dy) {
        module = mod;
        lecturer = lec;
        room = rm;
        fromTime = from;
        toTime = to;
        day = dy;
    }

    public String getModule() { return module; }
    public String getLecturer() { return lecturer; }
    public String getRoom() { return room; }
    public String getTime() { return fromTime + "-" + toTime; }
    public String getFromTime() { return fromTime; }
    public String getDay() { return day; }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("module", module)
                .add("lecturer", lecturer)
                .add("room", room)
                .add("fromTime", fromTime)
                .add("toTime", toTime)
                .add("day", day)
                .build();
    }

    public String toString() {
        return String.format("Module: %s, Lecturer: %s, Room: %s, Time: %s-%s, Day: %s",
                module, lecturer, room, fromTime, toTime, day);
    }
}