# TODO
## Client
### GUI
- [ ] Main Menu Screen
    - [x] Header
    - [x] Buttons for Course Selection
    - [x] Stop Button
    - [ ] Controller Logic
    - [x] Styling
### Actions
- [ ] Add Lecture
    - [x] Input Fields:
      - [x] Day
      - [X] Time 
      - [x] Room num
      - [x] module name
      - [X] lecturer name
      - [x] Change Input Fields to Correct Types (text field, Day Selection, Time Selection)
    - [x] Styling
    - [X] Handle server response
- [ ] Remove a Lecture
    - [ ] Button:
        - [ ] Lecture Time
        - [ ] Confirm prompt -> Contains Lecture details
    - [ ] Styling
    - [ ] Handle server response
- [ ] Display Schedule
    - [X] Parse Server response
    - [X] Format parsed response to timetable
    - [ ] Deal with lectures longer than 1 hour
- [ ] Other 
    - [ ] Custom User input
    - [ ] Handle Server Response

## Server
### Actions
- [X] Add Lecture
  - [X] Basic check for timeslot clashes
  - [X] From + To timeslot check
  - [X] Success message
- [ ] Remove a Lecture
    - [ ] Success Messages (include room number)
- [X] Display Schedule
  - [X] Send data structure to client
- [ ] Other -> Custom User input
  - [ ] IncorrectActionException

## Bugs - Add as They Occur
- [X] Bug with Sockets when Switching Between Views - App crashes after having switched view and attempting to close the server
- [X] Bug with loading Timetable data when starting app before server - No logic to re-fetch timetable once connection to server has been reestablished
- [x] Bug with displaying "Added Lectures" on timetable after connection to server has been restarted - Client keeps previous "Added Lecture and does not remove it - potential flushing issue
- [ ] Bug with loading of scenes - when going between scenes and the application happens to be a differerent size than what was initialised (so not 1280x720), it will resize itself to 1280x720 from whatever the previous window size was. So if you are in fullscreen and switch to timetable view, it will resize the window to what was initialised as 1280x720

## Submission Requirements
- studentId1_studentId2.zip