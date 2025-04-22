package ul.cs4076project.Model;

public sealed interface ResponseType {
    record StringResponse(String value) implements ResponseType {
    }

    record TimetableResponse(Lecture[][] value) implements ResponseType {
    }
}
