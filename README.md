# Project Details
## Authors:
23388986 : Sam Smith <br>
23361077 : Ilias kourousis <br>

## Documentation:
### Running the Project:
#### General:
- Ensure you have Java 23 installed on your machine.

#### Server:
The projects **database** is hosted on a **VPS** and the schema is viewable down below,<br>
however if you wish to run the server locally you must:
1. Install PostgresSQL on your machine. https://www.postgresql.org/download/windows/
2. Create a user with the details in the `Server.java` file or make your own and update the `Server.java` file.
3. Grant user permissions to set up the database. https://www.postgresql.org/docs/8.1/privileges.html
4. Run the schema queries in the schema below. 

As for the **Server** itself, it should work fine on Netbeans being a Maven project.

#### Client:
You have 2 ways of running the client:
1. Run the binary: `{project_root}/Client/target/cs4076-client/bin/launcher` folder. 
- If you need to re-create the binary, navigate to the `{project_root}/Client` directory.
```bash
mvn clean javafx:jlink -X
```
2. Import the project into Netbeans and run it from there.
- Ensure you have JavaFX is installed on your machine.

### Project Info:
#### Server/API:
##### Run:
To run the server is can be run by:
1. Navigate to the directory `{project_root}/Server`.
2. Run the following command:
```bash
./mvnw exec:java -Dexec.mainClass="ul.cs4076projectserver.App"
```

#### Database Schema:
**Objects:** <br>
Custom enum type for days of the week.
Custom enum type for modules.
```sql
CREATE TYPE day_of_week AS ENUM ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY');

CREATE TYPE modules AS ENUM ('CS4006', 'CS4076', 'CS4115', 'CS4815', 'MA4413');
```

**Indexes:** <br>
Index to improve overlap query performance.
```sql
CREATE INDEX idx_lectures_overlap ON lectures (day, from_time, to_time);
```

**Tables:** <br>
Used the day_of_week & module enum type for the day & module columns to ensure that only valid days & modules are entered.
Unique Key constraint on the day and from_time columns to ensure that no two lectures are scheduled at the same time on the same day.
```sql
CREATE TABLE lectures (
    id SERIAL PRIMARY KEY,
    module modules NOT NULL,
    lecturer VARCHAR(100) NOT NULL,
    room VARCHAR(50) NOT NULL,
    from_time CHAR(5) NOT NULL, 
    to_time CHAR(5) NOT NULL, 
    day day_of_week NOT NULL,
    UNIQUE(day, from_time)
);
```



