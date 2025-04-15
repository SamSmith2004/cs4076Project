package ul.cs4076projectserver.Handlers;

import ul.cs4076projectserver.Models.DayOfWeek;
import ul.cs4076projectserver.Models.Lecture;
import ul.cs4076projectserver.Models.Module;
import ul.cs4076projectserver.Server;

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

    public boolean updateLecture(Lecture lecture) throws SQLException {
        String query = "UPDATE lectures SET module = ?, lecturer = ?, room = ?, from_time = ?, to_time = ?, day = ? WHERE id = ?";
        boolean success = false;

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setObject(1, lecture.getModule(), Types.OTHER);
                pstmt.setString(2, lecture.getLecturer());
                pstmt.setString(3, lecture.getRoom());
                pstmt.setString(4, lecture.getFromTime());
                pstmt.setString(5, lecture.getToTime());
                pstmt.setObject(6, lecture.getDay(), Types.OTHER);
                pstmt.setInt(7, lecture.getId());

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
     * Remove a lecture from the database given the lecture ID.
     *
     * @param id The ID of the lecture to remove
     * @return {@code true} If the operation was successful, {@code false}
     * otherwise.
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

    public boolean lectureOverlaps(DayOfWeek day, String fromTime, String toTime) throws SQLException {
        // This first check is necessary due to the UNIQUE(day, from_time) constraint which runs before the overlap check.
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

        // If no exact match, check for overlaps
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

    public DayOfWeek getEarlyLectures(DayOfWeek day) throws SQLException {
        String selectQuery = "SELECT * FROM lectures WHERE day = ? ORDER BY to_timestamp(from_time, 'HH24:MI')";
        ArrayList<Lecture> lectures = new ArrayList<>();
        connection.setAutoCommit(false);
        boolean updateMade = false;

        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setObject(1, day, Types.OTHER);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                Lecture lec = new Lecture(
                        id,
                        Module.valueOf(rs.getString("module")),
                        rs.getString("lecturer"),
                        rs.getString("room"),
                        rs.getString("from_time").trim(),
                        rs.getString("to_time").trim(),
                        day
                );
                lectures.add(lec);
            }
        }

        if (lectures.isEmpty()) {
            connection.commit();
            return null;
        }

        try {
            int targetHour = 9; // 9AM
            String updateQuery = "UPDATE lectures SET from_time = ?, to_time = ? WHERE id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                for (Lecture lec : lectures) {
                    String targetFromTime = String.format("%d:00", targetHour);
                    String targetToTime = String.format("%d:00", targetHour + 1);

                    // Only update if lec not already in target time
                    int currentHour;
                    try {
                        currentHour = Integer.parseInt(lec.getFromTime().split(":")[0]);
                    } catch (NumberFormatException e) {
                        currentHour = targetHour + 1; // Just move on if it hits the fan
                    }

                    if (currentHour != targetHour) {
                        updateStmt.setString(1, targetFromTime);
                        updateStmt.setString(2, targetToTime);
                        updateStmt.setInt(3, lec.getId());
                        updateStmt.addBatch();
                        updateMade = true;
                    }
                    targetHour++;
                }
                updateStmt.executeBatch();
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
        return updateMade ? day : null;
    }
}