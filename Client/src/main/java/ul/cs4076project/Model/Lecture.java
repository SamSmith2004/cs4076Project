package ul.cs4076project.Model;

import javax.json.Json;
import javax.json.JsonObject;

public class Lecture {
    private String module;
    private String lecturer;
    private String room;
    private String time;
    private String day;

    public Lecture(String mod, String lec, String rm, String tm, String dy) {
        module = mod;
        lecturer = lec;
        room = rm;
        time = tm;
        day = dy;
    }

    public String getModule() { return module; }
    public String getLecturer() { return lecturer; }
    public String getRoom() { return room; }
    public String getTime() { return time; }
    public String getDay() { return day; }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("module", module)
                .add("lecturer", lecturer)
                .add("room", room)
                .add("time", time)
                .add("day", day)
                .build();
    }

    public String toString() {
        return "Module: " + module + ", Lecturer: " + lecturer + ", Room: " + room + ", Time: " + time + ", Day: " + day;
    }
}