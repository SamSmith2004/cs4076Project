package ul.Server.Handlers;

import ul.Server.Models.DayOfWeek;
import ul.Server.Models.Lecture;
import ul.Server.Models.Module;

import java.sql.*;
import java.util.ArrayList;

/**
 * The {@code DBManager} class provides methods to interact with the database.
 * It allows for the retrieval, addition, and removal of lectures. This class
 * uses a {@link Connection} object to perform SQL operations and
 * ensures data integrity through the use of transactions.
 */
public class DBManager {
    /**
     * The connection object used to interact with the database.
     */
    private final Connection connection;

    /**
     * Constructs a {@link DBManager} with the specified database connection.
     * 
     * @param connection The {@link Connection} object used to interact with the
     *                   database.
     */
    public DBManager(Connection connection) {
        this.connection = connection;
    }

    /**
     * All the lectures are retrived from the database through the use of an SQL
     * command. Each lecture from the Lecture db table has it's properites broken
     * down into:
     * <ul>
     * <li>{@link String} lecturer</li>
     * <li>{@link String} room</li>
     * <li>{@link String} fromTime</li>
     * <li>{@link String} toTime</li>
     * <li>{@link DayOfWeek} day</li>
     * <li>{@link</li>
     * </ul>
     * and created into a {@link Lecture} java
     * object which subsequently gets added to an {@link ArrayList<Lecture>} which
     * is ready to be processed further by other methods.
     * 
     * @return An {@link ArrayList<Lecture>} of relevant timetable data.
     * @throws SQLException If a database error occurs.
     * @see ul.Server.Models.Lecture
     * @see ul.Server.Models.DayOfWeek
     * @see ul.Server.Models.Module
     */
    public ArrayList<Lecture> getLectures() throws SQLException {
        ArrayList<Lecture> lectures = new ArrayList<>();
        String query = "SELECT * FROM lectures";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                ul.Server.Models.Module module = Module.valueOf(rs.getString("module"));
                String lecturer = rs.getString("lecturer");
                String room = rs.getString("room");
                String fromTime = rs.getString("from_time").trim();
                String toTime = rs.getString("to_time").trim();
                DayOfWeek day = DayOfWeek.valueOf(rs.getString("day"));

                // Remove leading zeros from times to maintain consistency
                String normalizedFromTime = fromTime.replaceFirst("^0", "");
                String normalizedToTime = toTime.replaceFirst("^0", "");

                lectures.add(new Lecture(id, module, lecturer, room, normalizedFromTime, normalizedToTime, day));
            }
        }

        return lectures;
    }

    /**
     * Add a lecture to the database through the use of a SQL command. A
     * {@link Lecture} object is passed in as the parameter which contains all the
     * Lecture properties. These include:
     * <ul>
     * <li>{@link String} lecturer</li>
     * <li>{@link String} room</li>
     * <li>{@link String} fromTime</li>
     * <li>{@link String} toTime</li>
     * <li>{@link DayOfWeek} day</li>
     * <li>{@link</li>
     * </ul>
     * 
     * @param lecture Lecture object containing all the relevant properties.
     * @return {@code true} If the operation was successfull, {@code false}
     *         otherwise.
     * @throws SQLException If a database access error occurs.
     * @see ul.Server.Models.Lecture
     * @see ul.Server.Models.DayOfWeek
     */
    public boolean addLecture(Lecture lecture) throws SQLException {
        String query = "INSERT INTO lectures (module, lecturer, room, from_time, to_time, day) VALUES (?, ?, ?, ?, ?, ?)";
        boolean success = false;

        try {
            // Start transaction (Ensure data integrity)
            connection.setAutoCommit(false);

            // Prepared statement to prevent SQL injection
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                // setObject() + Types.OTHER is used to store custom ENUM types in the database
                // Next time just going use a string wasted 2 hours on Type safety
                pstmt.setObject(1, lecture.getModule(), Types.OTHER);
                pstmt.setString(2, lecture.getLecturer());
                pstmt.setString(3, lecture.getRoom());
                pstmt.setString(4, lecture.getFromTime());
                pstmt.setString(5, lecture.getToTime());
                pstmt.setObject(6, lecture.getDay(), Types.OTHER);

                int affectedRows = pstmt.executeUpdate();
                success = affectedRows > 0;
            }

            if (success) {
                connection.commit();
            } else {
                connection.rollback();
            }

        } catch (SQLException e) {
            connection.rollback();
            throw e; // Re-throw to be handled by the caller
        } finally {
            // Ends transaction
            connection.setAutoCommit(true);
        }

        return success;
    }

    /**
     * Remove a lecture from the database given the lecture ID.
     * 
     * @param id The ID of the lecture to remove
     * @return {@code true} If the operation was successfull, {@code false}
     *         otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean removeLecture(int id) throws SQLException {
        String query = "DELETE FROM lectures WHERE id = ?";
        boolean success = false;

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, id);

                int affectedRows = pstmt.executeUpdate();
                success = affectedRows > 0;
            }

            if (success) {
                connection.commit();
            } else {
                connection.rollback();
            }

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

        return success;
    }

    /**
     * Check if a lecture overlaps with existing lectures in the database. The
     * method first checks if there is an exact matc hfor the lecture's day and
     * start time. If no exact match is found, it then checks for any overlapping
     * lectures on the same day. An overlap is defined by an existing lecture
     * starting before the new lecture ending and ends after the new lecture starts.
     * 
     * @param day      The {@link DayOfWeek} of the lecture.
     * @param fromTime {@link String} The start time of the lecture, in range
     *                 09:00-17:00.
     * @param toTime   {@link String} The end time of the lecture, in range
     *                 10:00-18:00.
     * @return {@code true} if the lecture overlaps with existing lectures,
     *         {@code false} otherwise.
     * @throws SQLException If a database error occurs.
     */
    public boolean lectureOverlaps(DayOfWeek day, String fromTime, String toTime) throws SQLException {
        //// This first check is necessary due to the UNIQUE(day, from_time) constraint which runs before the overlap check.
        // Check if there's already a lecture at the exact same time
        String exactMatchQuery = "SELECT COUNT(*) FROM lectures WHERE day = ? AND from_time = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(exactMatchQuery)) {
            pstmt.setObject(1, day, Types.OTHER);
            pstmt.setString(2, fromTime);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Exact time slot already taken");
                    return true;
                }
            }
        }

        //// If no exact match, check for overlaps
        // Checks if there's a lecture that starts before the new lecture ends and ends
        // after the new lecture starts
        String overlapQuery = "SELECT COUNT(*) FROM lectures WHERE day = ? AND " +
                "to_timestamp(to_time, 'HH24:MI') > to_timestamp(?, 'HH24:MI') AND " +
                "to_timestamp(from_time, 'HH24:MI') < to_timestamp(?, 'HH24:MI')";
        try (PreparedStatement pstmt = connection.prepareStatement(overlapQuery)) {
            pstmt.setObject(1, day, Types.OTHER);
            pstmt.setString(2, fromTime);
            pstmt.setString(3, toTime);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Returns true if count > 0 (overlap exists)
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception during overlap check: " + e.getMessage());
            throw e;
        }

        return false;
    }
}