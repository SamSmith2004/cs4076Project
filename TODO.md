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
    - [ ] Button:
      - [ ] Date
      - [ ] Time 
      - [ ] Room num
      - [ ] module name
    - [ ] Handle server response
- [ ] Remove a Lecture
    - [ ] Button:
        - [ ] Lecture Time
        - [ ] Confirm prompt -> Contains Lecture details
    - [ ] Handle server response
- [ ] Display Schedule
    - [ ] Parse Server response
    - [ ] Format parsed response to timetable
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

## Submission Requirements
- studentId1_studentId2.zip