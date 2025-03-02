package ul.cs4076project.Model;

public class Lecture {
    private String id;
    private Module module;
    private String lecturer;
    private String room;
    private String fromTime;
    private String toTime;
    private DayOfWeek day;

    public Lecture(String id, Module mod, String lec, String rm, String from, String to, DayOfWeek dy) {
        this.id = id;
        module = mod;
        lecturer = lec;
        room = rm;
        fromTime = from;
        toTime = to;
        day = dy;
    }

    public String getId() { return id; }
    public Module getModule() { return module; }
    public String getModuleString() { return module.name(); }
    public String getLecturer() { return lecturer; }
    public String getRoom() { return room; }
    public String getTime() { return fromTime + "-" + toTime; }
    public String getFromTime() { return fromTime; }
    public DayOfWeek getDay() { return day; }
    public String getDayString() { return day.name(); }

    public String toString() {
        return String.format("Module: %s, Lecturer: %s, Room: %s, Time: %s-%s, Day: %s",
                module, lecturer, room, fromTime, toTime, day);
    }
}