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

CREATE TYPE modules AS ENUM ('CS4006', 'CS4076', 'CS4115', 'CS4185', 'MA4413');
```

**Tables:** <br>
Used the day_of_week enum type for the day column to ensure that only valid days are entered.
Added a check to ensure times are within the valid range of `09:00` - `17:00` for the from_time column and `10:00` - `18:00` for the to_time column.
Unique Key constraint on the day and from_time columns to ensure that no two lectures are scheduled at the same time on the same day.
```sql
CREATE TABLE lectures (
    id SERIAL PRIMARY KEY,
    module modules NOT NULL,
    lecturer VARCHAR(100) NOT NULL,
    room VARCHAR(50) NOT NULL,
    from_time CHAR(5) NOT NULL CHECK (from_time >= '09:00' AND from_time <= '17:00'),
    to_time CHAR(5) NOT NULL CHECK (to_time >= '10:00' AND to_time <= '18:00'),
    day day_of_week NOT NULL,
    UNIQUE(day, from_time)
);
```



