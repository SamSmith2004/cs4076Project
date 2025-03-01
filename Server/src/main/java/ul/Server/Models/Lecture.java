package ul.Server.Models;

public class Lecture {
    private final int id;
    private final Module module;
    private final String lecturer;
    private final String room;
    private final String fromTime;
    private final String toTime;
    private final DayOfWeek day;

    // Constructor with ID
    public Lecture(int id, Module mod, String lec, String rm, String from, String to, DayOfWeek dy) {
        this.id = id;
        module = mod;
        lecturer = lec;
        room = rm;
        fromTime = from;
        toTime = to;
        day = dy;
    }

    // Constructor without ID (Init before addition to DB)
    public Lecture(Module mod, String lec, String rm, String from, String to, DayOfWeek dy) {
        this.id = -1; // Temporary ID, will be replaced when added to database
        module = mod;
        lecturer = lec;
        room = rm;
        fromTime = from;
        toTime = to;
        day = dy;
    }

    public int getId() { return id; }
    public Module getModule() { return module; }
    public String getModuleString() { return module.name(); }
    public String getLecturer() { return lecturer; }
    public String getRoom() { return room; }
    public String getFromTime() { return fromTime; }
    public String getToTime() { return toTime; }
    public DayOfWeek getDay() { return day; }
    public String getDayString() { return day.name(); }

    public String toString() {
        return String.format("ID: %d, Module: %s, Lecturer: %s, Room: %s, Time: %s-%s, Day: %s",
                id, module, lecturer, room, fromTime, toTime, day);
    }
}