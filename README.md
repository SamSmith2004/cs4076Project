# Project Details
## Authors:
x

y

## Documentation:
### Running the Project:
#### Server:
- PGSQL download guide (TODO)
- PGSQL import guide (TODO)
- Server should just run on Netbeans

#### Client:
- TODO

### Project Info:
#### Server/API:
TODO

#### Client:
TODO

#### Database:
##### Schema:
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



