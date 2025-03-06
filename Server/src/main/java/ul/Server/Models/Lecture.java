package ul.Server.Models;

/**
 * The {@code Lecture} class represents a lecture with its associated details.
 * It includes information such as the lecture ID, module, lecturer, room, time,
 * and day.
 * 
 * @see ul.Server.Models.DayOfWeek
 * @see ul.Server.Models.Module
 */
public class Lecture {
    /**
     * The ID of the lecture.
     */
    private final int id;
    /**
     * The module associated with the lecture.
     */
    private final Module module;
    /**
     * The lecturer conducting the lecture.
     */
    private final String lecturer;
    /**
     * The room where the lecture is held.
     */
    private final String room;
    /**
     * The start time of the lecture.
     */
    private final String fromTime;
    /**
     * The end time of the lecture.
     */
    private final String toTime;
    /**
     * The day of the week when the lecture is scheduled.
     */
    private final DayOfWeek day;

    /**
     * Constructs a {@code Lecture} with the specified ID and details.
     * 
     * @param id   The ID of the lecture.
     * @param mod  The module associated with the lecture.
     * @param lec  The lecturer teaching the lecture.
     * @param rm   The room where the lecture is held.
     * @param from The start time of the lecture.
     * @param to   The end time of the lecture.
     * @param dy   The day of the week when the lecture is scheduled.
     * @see ul.Server.Models.Module
     * @see ul.Server.Models.DayOfWeek
     */
    public Lecture(int id, Module mod, String lec, String rm, String from, String to, DayOfWeek dy) {
        this.id = id;
        module = mod;
        lecturer = lec;
        room = rm;
        fromTime = from;
        toTime = to;
        day = dy;
    }

    /**
     * Constructs a {@code Lecture} without an ID. This is not to be used to add to
     * the daabase.
     * 
     * @param mod  The module associated with the lecture.
     * @param lec  The lecturer teaching the lecture.
     * @param rm   The room where the lecture is held.
     * @param from The start time of the lecture.
     * @param to   The end time of the lecture.
     * @param dy   The day of the week when the lecture is scheduled.
     * @see ul.Server.Models.Module
     * @see ul.Server.Models.DayOfWeek
     */
    public Lecture(Module mod, String lec, String rm, String from, String to, DayOfWeek dy) {
        this.id = -1; // Temporary ID, will be replaced when added to database
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
     * @return The ID of the lecture.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the module associated with the lecture.
     * 
     * @return The module associated with the lecture.
     * @see ul.Server.Models.Module
     */
    public Module getModule() {
        return module;
    }

    /**
     * Gets the module name as a string.
     * 
     * @return The module name as a string.
     */
    public String getModuleString() {
        return module.name();
    }

    /**
     * Gets the lecturer teaching the lecture.
     * 
     * @return The lecturer conducting the lecture.
     */
    public String getLecturer() {
        return lecturer;
    }

    /**
     * Gets the room where the lecture is held.
     * 
     * @return The room where the lecture is held.
     */
    public String getRoom() {
        return room;
    }

    /**
     * Gets the start time of the lecture.
     * 
     * @return The start time of the lecture.
     */
    public String getFromTime() {
        return fromTime;
    }

    /**
     * Gets the end time of the lecture.
     * 
     * @return The end time of the lecture.
     */
    public String getToTime() {
        return toTime;
    }

    /**
     * Gets the day of the week when the lecture is scheduled.
     * 
     * @return The day of the week when the lecture is scheduled.
     * @see ul.Server.Models.DayOfWeek
     */
    public DayOfWeek getDay() {
        return day;
    }

    /**
     * Gets the day of the week as a string.
     * 
     * @return The day of the week as a string.
     */
    public String getDayString() {
        return day.name();
    }

    /**
     * Returns a string representation of the lecture.
     * 
     * @return A string representation of the lecture.
     */
    @Override
    public String toString() {
        return String.format("ID: %d, Module: %s, Lecturer: %s, Room: %s, Time: %s-%s, Day: %s",
                id, module, lecturer, room, fromTime, toTime, day);
    }
}