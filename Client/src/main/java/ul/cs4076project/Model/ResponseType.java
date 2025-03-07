package ul.cs4076project.Model;

/**
 * ResponseType is a sealed interface that defines the types of responses
 * that can be returned from the server.
 * 
 * <p>
 * This interface is sealed, meaning only the specified classes can implement
 * it. It has two implementing records: {@link StringResponse} and
 * {@link TimetableResponse}.
 */

public sealed interface ResponseType {
    /**
     * Represents a response that contains a single string value.
     */
    record StringResponse(String value) implements ResponseType {
    }

    /**
     * Represents a response that contains a 2D array of {@link Lecture} objects,
     * representing a timetable.
     */
    record TimetableResponse(Lecture[][] value) implements ResponseType {
    }
}
