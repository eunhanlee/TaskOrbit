# TaskOrbit

TaskOrbit: Your tasks, on a clear, steady track.



Here is your entire specification rewritten \*\*clearly and professionally in English\*\*,

with \*\*no changes to meaning or logic\*\* — only clean translation and formatting.



---



\# \*\*0. Project Information\*\*



\- \*\*Project Name:\*\* TaskOrbit

\- \*\*Spring Boot Version:\*\* 4.0.0

\- \*\*Java Version:\*\* 21

\- \*\*Architecture:\*\* Frontend + Backend + PostgreSQL

\- \*\*Goal:\*\* A web-based task management system accessible from anywhere



---



\# \*\*1. Backend\*\*



\- \*\*Java 21\*\*

\- \*\*Spring Boot 4.0.0\*\*

\- \*\*JPA (Hibernate)\*\*

\- \*\*JWT Authentication\*\*

\- \*\*CORS Configuration Required\*\*

\- \*\*Flyway (Database Migration Management)\*\*

\- \*\*Base Package:\*\* `com.eunhanlee.taskorbit`



---



\# \*\*2. Frontend\*\*



\- \*\*React\*\*

\- \*\*Vite-based development environment\*\*

\- \*\*Tailwind CSS\*\*

\- \*\*React Router\*\*

\- \*\*State Management (future choice): React Query or Zustand\*\*



---



\# \*\*3. Database\*\*



\- \*\*PostgreSQL\*\*

\- \*\*Schema managed with Flyway migrations\*\*

\- \*\*Hosted via Docker (local dev) or Render PostgreSQL (production)\*\*



---



\# \*\*4. Deployment\*\*



\- \*\*Render Platform (low-cost / free options available)\*\*

&nbsp;   - Backend → Render Web Service

&nbsp;   - Frontend → Render Static Site

&nbsp;   - Database → Render PostgreSQL

\- HTTPS enabled automatically

\- GitHub → Render automatic deployment supported





----



\# \*\*TaskOrbit – Specification Document\*\*



\## \*\*Overview\*\*



This document describes the core design and workflow for the TaskOrbit task-management application.



---



\# \*\*✔ Table 1: Task Information\*\*



Each task contains the following fields:



\- \*\*Title\*\*: Short summary of the task

\- \*\*Category\*\*: High-level classification

\- \*\*Size\*\*: Under 10 min / under 30 min / over 1 hour

\- \*\*Status\*\*: Ongoing / Waiting / Done

\- \*\*Work Date\*\*: The original intended date for the task

\- \*\*Schedule Date\*\*: The date when the task should appear in “Today” (rolls over to next day if incomplete)



---



\# \*\*✔ Table 2: Task Log\*\*



Each log entry stores:



\- \*\*task\_id\*\*

\- \*\*Date\*\*

\- \*\*Content\*\* (progress or notes)

\- \*\*Next Action\*\*: One-line description of what to do next

\- \*\*History Log\*\*: Long-form text—accumulates daily notes over time



\*\*Displayed Next Action Rule:\*\*



→ The \*\*most recent\*\* log entry’s Next Action is always shown in Today/Later lists.



---



\# \*\*✔ Table 3: Task Completion Record\*\*



\- \*\*task\_id\*\*

\- \*\*Completed Date\*\* (the date when status changed to Done)



\*\*Done Behavior:\*\*



→ Tasks remain in the Done tab until \*\*3:00 AM the next day\*\*,



at which point they automatically move to the Record tab.



---



\# \*\*✔ Table 4: Recurring Task Settings\*\*



\- Data needed to generate repeating tasks

\- Recurrence conditions (e.g., Daily)



\*\*Note:\*\*



This table stores \*\*rules\*\* only.



Actual tasks are generated automatically based on these rules.



---



\# \*\*✔ Table 5: Global Log (Undo System)\*\*



\- Stores all changes in chronological order

\- Used to implement Undo/Redo functionality



---



\# \*\*✔ Task Management Workflow\*\*



\### \*\*1. There are five tabs:\*\*



1\. \*\*Today\*\*

2\. \*\*Done\*\*

3\. \*\*Record\*\*

4\. \*\*Later\*\*

5\. \*\*Repeat\*\*



---



\### \*\*2. Today Tab Rules\*\*



The Today tab shows all tasks where:



\- \*\*schedule\_date = today\*\*, or

\- \*\*schedule\_date < today\*\* → displayed with markers like “-1”, “-2”, etc.

\- Tasks that were in \*\*Waiting\*\* become \*\*Ongoing\*\* at 3:00 AM the next day



---



\### \*\*3. Later Tab Rules\*\*



The Later tab shows only:



\- Tasks where \*\*schedule\_date > today\*\*



---



\### \*\*4. Task Creation\*\*



When a user creates a new task:



\- `work\_date = today`

\- `schedule\_date = today`

\- Category = currently selected category (or “None” if none selected)



---



\### \*\*5. Completing a Task\*\*



\- When the user marks a task as complete → it moves to the \*\*Done\*\* tab



---



\### \*\*6. Done → Record Auto-Move\*\*



At \*\*3:00 AM\*\*, all tasks in the Done tab:



\- Move automatically to the \*\*Record\*\* tab



---



\### \*\*7. Incomplete Tasks\*\*



If a task is not completed:



\- Its \*\*schedule\_date\*\* automatically increments to the next day

\- It appears in Today with a “-1 / -2 / …” delay marker

\- \*\*work\_date never changes\*\*



---



\### \*\*8. Waiting Status\*\*



If a task is marked as Waiting:



\- At \*\*3:00 AM\*\*, it automatically returns to \*\*Ongoing\*\* status

\- It appears in Today based on its schedule\_date



---



\### \*\*9. Task Editing\*\*



\- Tasks can be edited from \*\*any tab\*\*



---



\### \*\*10. Recurring Tasks\*\*



\- Recurrence rules generate tasks automatically each day

\- No additional logic beyond creation is applied



---



\### \*\*11. Task Display Format\*\*



Each task displays:



\- Completion checkbox

\- Title

\- Next Action

\- Waiting button

\- Date



---



\### \*\*12. Tab Features\*\*



Each tab supports:



\- Category filtering

\- Undo/Redo navigation

\- Sorting options

