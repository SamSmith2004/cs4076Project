package ul.Server.Utils;

import java.util.ArrayList;

public class SessionData {
    ArrayList<Lecture> timeTable =  new ArrayList<>();

    public SessionData() {}

    public void addLecture(Lecture lecture) {
        timeTable.add(lecture);
    }

    public ArrayList<Lecture> getTimeTable() {
        return timeTable;
    }

    public void clearTimeTable() {
        timeTable.clear();
    }

    public void removeLecture(int index) {
        timeTable.remove(index);
    }

    public void editLecture(int index, Lecture lecture) {
        timeTable.set(index, lecture);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Lecture lecture : timeTable) {
            sb.append(lecture.toString());
            sb.append("\n");
        }
        return sb.toString();
    }


    public void fillMockData() {
        timeTable.add(new Lecture("CS101", "Dr. Smith", "Room 101", "9:00", "Monday"));
        timeTable.add(new Lecture("CS102", "Dr. Smith", "Room 102", "10:00", "Tuesday"));
        timeTable.add(new Lecture("CS103", "Dr. Smith", "Room 103", "11:00", "Wednesday"));
        timeTable.add(new Lecture("CS104", "Dr. Smith", "Room 104", "12:00", "Thursday"));
        timeTable.add(new Lecture("CS105", "Dr. Smith", "Room 105", "13:00", "Friday"));
    }
}