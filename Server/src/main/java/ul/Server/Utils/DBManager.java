package ul.Server.Utils;

import java.sql.*;
import java.util.ArrayList;

public class DBManager {
    private final Connection connection;

    public DBManager(Connection connection) {
        this.connection = connection;
    }

    /**
     * Get all lectures from the database
     * @return ArrayList<Lecture> timetable data
     */
    public ArrayList<Lecture> getLectures() throws SQLException {
        ArrayList<Lecture> lectures = new ArrayList<>();
        String query = "SELECT * FROM lectures";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                Module module = Module.valueOf(rs.getString("module"));
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
     * Add a lecture to the database
     * @param lecture Lecture object to add
     * @return boolean success
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
     * Update a lecture in the database
     * @param id id of the lecture to update
     * @return boolean success
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
     * Check if a lecture overlaps with existing lectures
     * @param day ENUM DayOfWeek
     * @param fromTime String in range 09:00-17:00
     * @param toTime String in range 10:00-18:00
     * @return boolean isOverlapping
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
        // Checks if there's a lecture that starts before the new lecture ends and ends after the new lecture starts
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