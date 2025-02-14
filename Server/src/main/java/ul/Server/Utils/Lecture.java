package ul.Server.Utils;

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
}
