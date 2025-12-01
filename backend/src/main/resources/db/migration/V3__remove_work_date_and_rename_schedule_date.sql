-- Remove work_date column and rename schedule_date to due_date
ALTER TABLE tasks DROP COLUMN IF EXISTS work_date;
ALTER TABLE tasks RENAME COLUMN schedule_date TO due_date;

-- Update index
DROP INDEX IF EXISTS idx_tasks_schedule_date;
DROP INDEX IF EXISTS idx_tasks_work_date;
CREATE INDEX idx_tasks_due_date ON tasks(due_date);


