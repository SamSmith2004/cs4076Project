package ul.Server.Utils;

public class Lecture {
    private final String module;
    private final String lecturer;
    private final String room;
    private final String fromTime;
    private final String toTime;
    private final String day;

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
    public String getToTime() { return toTime; }
    public String getDay() { return day; }

    public boolean overlaps(Lecture other) {
        if (!this.day.equals(other.day)) return false;

        int thisFrom = Integer.parseInt(this.fromTime.replace(":", ""));
        int thisTo = Integer.parseInt(this.toTime.replace(":", ""));
        int otherFrom = Integer.parseInt(other.fromTime.replace(":", ""));
        int otherTo = Integer.parseInt(other.toTime.replace(":", ""));

        return (thisFrom < otherTo && thisTo > otherFrom);
    }

    public String toString() {
        return String.format("Module: %s, Lecturer: %s, Room: %s, Time: %s-%s, Day: %s",
                module, lecturer, room, fromTime, toTime, day);
    }
}
