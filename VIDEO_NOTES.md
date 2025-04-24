- 7-8 min video

### 3 FEATURES TO DEMONSTRATE

show on client -> diagram overview of code and how it links -> show & explain code 

1. Early Lectures
2. Replace
3. Add

##### EARLY LECTURES
- Client
    - resources/timetable-view.fxml:49-57
    - Controller/TimetableController.java:255-300 onEarlyLectureButtonClick()
    - Model/TCPClient.java:81-89 update()
    - Model/TCPClient.java:113-166 sendRequest()
    - Model/ResponseHandler.java:15-49 extractResponse()

- Server
    - Server.java:198-335 ClientHandler
    - Server.java:260 Update update = new Update(requestData);
    - Handlers/Update.java:146-183 buildEarlyLectureResponse()

##### REPLACE
- Client
    - resources/popup-dialogues/replace-lecture-popup-dialogue.fxml:11-77
    - Controller/ReplaceLecturePopupDialogueController.java:33-135 handleSubmitBtn()
    - Model/TCPClient.java:71-79 post()
    - Model/TCPClient.java:113-166 sendRequest()
    - Model/ResponseHandler.java:15-49 extractResponse()
    
- Server
    - Server.java:198-335 ClientHandler
    - Server.java:260 Update update = new Update(requestData);
    - Handlers/Update.java:49-144 buildUpdateLectureResponse()

##### ADD
- Client
    - resources/popup-dialogues/add-lecture-popup-dialogue.fxml:11-77
    - Controller/AddALecturePopupDialogueController.java:119-205 handleOkButton()
    - Model/TCPClient.java:71-79 post()
    - Model/TCPClient.java:113-166 sendRequest()
    - Model/ResponseHandler.java:15-49 extractResponse()
    
- Server
    - Server.java:198-335 ClientHandler
    - Server.java:255 Post post = new Post(requestData);
    - Handlers/Post.java:58-170 buildAddLectureResponse()

### ADDITIONAL THINGS TO DEMONSTRATE

1. How to run and compile project to run outside of netbeans