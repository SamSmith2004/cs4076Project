package ul.cs4076project.Model;

// ResponseType is a sealed interface that defines the types of responses that can be returned from the server
public sealed interface ResponseType {
    record StringResponse(String value) implements ResponseType {}
    record TimetableResponse(Lecture[][] value) implements ResponseType {}
}
