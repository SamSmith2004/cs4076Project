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
    - [ ] Input Fields:
      - [x] Day
      - [X] Time 
      - [x] Room num
      - [x] module name
    - [ ] Change Input Fields to Correct Types (text field, Day Selection, Time Selection)
    - [ ] Handle server response
- [ ] Remove a Lecture
    - [ ] Button:
        - [ ] Lecture Time
        - [ ] Confirm prompt -> Contains Lecture details
    - [ ] Handle server response
- [X] Display Schedule
    - [X] Parse Server response
    - [X] Format parsed response to timetable
- [ ] Other 
    - [ ] Custom User input
    - [ ] Handle Server Response

## Server
### Actions
- [ ] Add Lecture
  - [ ] Check for timeslot clashes
  - [ ] Success message
- [ ] Remove a Lecture
    - [ ] Success Messages (include room number)
- [X] Display Schedule
  - [X] Send data structure to client
- [ ] Other -> Custom User input
  - [ ] IncorrectActionException

## Bugs
- [X] Bug with Sockets when Switching Between Views - App crashes after having switched view and attempting to close the server
- [ ] Bug with loading Timetable data when starting app before server - No logic to refetch timetable once connection to server has been reestablished

## Submission Requirements
- studentId1_studentId2.zip