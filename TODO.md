# TODO
## Client
### GUI
- [X] Main Menu Screen
    - [x] Header
    - [x] Buttons for Course Selection
    - [x] Stop Button
    - [X] Controller Logic
    - [x] Styling
### Actions
- [x] Add Lecture
    - [x] Input Fields:
      - [x] Day
      - [X] Time 
      - [x] Room num
      - [x] module name
      - [X] lecturer name
      - [x] Change Input Fields to Correct Types (text field, Day Selection, Time Selection)
    - [x] Styling
    - [X] Handle server response
- [X] Remove a Lecture
    - [X] Button:
        - [x] Lecture Time
        - [X] Confirm prompt -> Contains Lecture details
    - [X] Logic
        - [X] Event Handlers For All Input Fields (If They Are Populated)
        - [X] Conditional Logic to Render Lecture Details and "REMOVE Lecture" Button Once Fields are Filled In
    - [X] Styling
        - [X] Auto Resizable Window Once All Fields Have Been Filled Out and Lecture Details are Displayed
        - [X] Style Lecture Details on Popup Window
    - [X] Handle server response
- [X] Display Schedule
    - [X] Parse Server response
    - [X] Format parsed response to timetable
- [X] Other 
    - [X] Custom User input
    - [X] Handle Server Response

## Server
### Actions
- [X] Add Lecture
  - [X] Basic check for timeslot clashes
  - [X] From + To timeslot check
  - [X] Success message
- [X] Remove a Lecture
    - [X] Return removed lecture data
- [X] Display Schedule
  - [X] Send data structure to client
- [X] Other -> Custom User input
  - [X] IncorrectActionException

## Bugs - Add as They Occur
- [X] Bug with Sockets when Switching Between Views - App crashes after having switched view and attempting to close the server
- [X] Bug with loading Timetable data when starting app before server - No logic to re-fetch timetable once connection to server has been reestablished
- [x] Bug with displaying "Added Lectures" on timetable after connection to server has been restarted - Client keeps previous "Added Lecture and does not remove it - potential flushing issue
- [ ] Bug with loading of scenes - when going between scenes and the application happens to be a differerent size than what was initialised (so not 1280x720), it will resize itself to 1280x720 from whatever the previous window size was. So if you are in fullscreen and switch to timetable view, it will resize the window to what was initialised as 1280x720
- [X] RemoveLectureDialogue Edge cases
  - [X] Order of selection bug 
- [X] Connection error message bug on remove stage

## Polishing
- [ ] Replace general exceptions with correct explicit exceptions
- [ ] Update notice labels to reflect user-friendly messages
- [X] Fix Types for API
- [ ] Styling
  - [ ] Timetable
  - [ ] Main Menu
  - [ ] Add Lecture
  - [ ] Remove Lecture
- [ ] Remove redundancies

## Submission Requirements
- studentId1_studentId2.zip