package ul.cs4076project.Model;

/**
 * The {@code Lecture} class represents a lecture with its associated details.
 * It includes information such as the lecture ID, module, lecturer, room, time,
 * and day.
 * 
 * @see ul.cs4076project.Model.DayOfWeek
 * @see ul.cs4076project.Model.Module
 */
public class Lecture {
    /**
     * The ID of the lecture.
     */
    private String id;
    /**
     * The module associated with the lecture.
     */
    private Module module;
    /**
     * The lecturer conducting the lecture.
     */
    private String lecturer;
    /**
     * The room where the lecture is held
     */
    private String room;
    /**
     * The star ttime of the lecture.
     */
    private String fromTime;
    /**
     * The end time of the lecture.
     */
    private String toTime;
    /**
     * The day of the week when the lecture is scheduled.
     */
    private DayOfWeek day;

    /**
     * Constructs a {@code Lecture} with the specified ID and details.
     * 
     * @param id   The ID of the lecture.
     * @param mod  The module associated with the lecture.
     * @param lec  The lecturer teaching the lecture.
     * @param rm   The room where the lecture is held.
     * @param from The start time of the lecture.
     * @param to   The end time of the lecture.
     * @param dy   The day of the week when the lecture is held.
     * @see ul.cs4076project.Model.Module
     * @see ul.cs4076project.Model.DayOfWeek
     */
    public Lecture(String id, Module mod, String lec, String rm, String from, String to, DayOfWeek dy) {
        this.id = id;
        module = mod;
        lecturer = lec;
        room = rm;
        fromTime = from;
        toTime = to;
        day = dy;
    }

    /**
     * Gets the ID of the lecture.
     * 
     * @return A {@link String} of the lecture's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the module associated with the lecture as a {@link Module} object.
     * 
     * @return A {@link Module} objcet
     */
    public Module getModule() {
        return module;
    }

    /**
     * Gets the module associated with the lecture as a {@link String} as oppoosed
     * to a
     * {@link Module} object.
     * 
     * @return A {@link String} of the module name
     * @see ul.cs4076project.Model.Module
     */
    public String getModuleString() {
        return module.name();
    }

    /**
     * Gets the lecturer teaching the lecture.
     * 
     * @return A {@link String} of the lecturer conducting the module
     */
    public String getLecturer() {
        return lecturer;
    }

    /**
     * Gets the room where the lecture is held.
     * 
     * @return A {@link String} of the room name.
     */
    public String getRoom() {
        return room;
    }

    /**
     * Gets the running time of the lecture.
     * 
     * @return A {@link String} of the running time of the lecture.
     */
    public String getTime() {
        return fromTime + "-" + toTime;
    }

    /**
     * Gets the start time of the lecture.
     * 
     * @return A {@link String} of the start time of the lecture.
     */
    public String getFromTime() {
        return fromTime;
    }

    /**
     * Gets the day of the week when the lecture is scheduled.
     * 
     * @return A {@link DayOfWeek} object of the day of the week when the lecture is
     *         scheduled.
     * @see ul.cs4076project.Model.DayOfWeek
     */
    public DayOfWeek getDay() {
        return day;
    }

    /**
     * Gets the day of the week as a string.
     * 
     * @return A {@link String} of the day of the week as a string.
     */
    public String getDayString() {
        return day.name();
    }

    /**
     * Returns a string representation of the lecture.
     * 
     * @return A {@link String} representation of the lecture.
     */
    public String toString() {
        return String.format("Module: %s, Lecturer: %s, Room: %s, Time: %s-%s, Day: %s",
                module, lecturer, room, fromTime, toTime, day);
    }
}