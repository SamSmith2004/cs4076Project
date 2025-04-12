package ul.cs4076projectserver.Models;

import java.lang.Module;

/**
 * The {@code Lecture} class represents a lecture with its associated details.
 * It includes information such as the lecture ID, module, lecturer, room, time,
 * and day.
 * 
 * @see ul.cs4076projectserver.Models.DayOfWeek
 * @see ul.cs4076projectserver.Models.Module
 */
public class Lecture {
    /**
     * The ID of the lecture.
     */
    private final int id;
    /**
     * The module associated with the lecture.
     */
    private final ul.cs4076projectserver.Models.Module module;
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
     * @see ul.cs4076projectserver.Models.Module
     * @see ul.cs4076projectserver.Models.DayOfWeek
     */
    public Lecture(int id, ul.cs4076projectserver.Models.Module mod, String lec, String rm, String from, String to, DayOfWeek dy) {
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
     * the database.
     * 
     * @param mod  The module associated with the lecture.
     * @param lec  The lecturer teaching the lecture.
     * @param rm   The room where the lecture is held.
     * @param from The start time of the lecture.
     * @param to   The end time of the lecture.
     * @param dy   The day of the week when the lecture is scheduled.
     * @see ul.cs4076projectserver.Models.Module
     * @see ul.cs4076projectserver.Models.DayOfWeek
     */
    public Lecture(ul.cs4076projectserver.Models.Module mod, String lec, String rm, String from, String to, DayOfWeek dy) {
        this.id = -1; // Temporary ID, will be replaced when added to database
        module = mod;
        lecturer = lec;
        room = rm;
        fromTime = from;
        toTime = to;
        day = dy;
    }

    public Lecture(String id, ul.cs4076projectserver.Models.Module mod, String lec, String rm, String from, String to, DayOfWeek dy) {
        this.id = Integer.parseInt(id);
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
     * @return The the lecture's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the module associated with the lecture.
     * 
     * @return The {@link java.lang.Module} associated with the lecture.
     * @see ul.cs4076projectserver.Models.Module
     */
    public ul.cs4076projectserver.Models.Module getModule() {
        return module;
    }

    /**
     * Gets the module name as a string.
     * 
     * @return The {@link Module} name as a string.
     */
    public String getModuleString() {
        return module.name();
    }

    /**
     * Gets the lecturer teaching the lecture.
     * 
     * @return A {@link String} of the lecturer conducting the lecture.
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
     * Gets the end time of the lecture.
     * 
     * @return A {@link String} of the end time of the lecture.
     */
    public String getToTime() {
        return toTime;
    }

    /**
     * Gets the day of the week when the lecture is scheduled.
     * 
     * @return A {@link DayOfWeek} object of the day of the week when the lecture is
     *         scheduled.
     * @see ul.cs4076projectserver.Models.DayOfWeek
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
    @Override
    public String toString() {
        return String.format("ID: %d, Module: %s, Lecturer: %s, Room: %s, Time: %s-%s, Day: %s",
                id, module, lecturer, room, fromTime, toTime, day);
    }
}