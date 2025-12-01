-- Table 1: Task Information
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    size VARCHAR(20) CHECK (size IN ('UNDER_10_MIN', 'UNDER_30_MIN', 'OVER_1_HOUR')),
    status VARCHAR(20) NOT NULL DEFAULT 'ONGOING' CHECK (status IN ('ONGOING', 'WAITING', 'DONE')),
    work_date DATE NOT NULL,
    schedule_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table 2: Task Log
CREATE TABLE task_logs (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    content TEXT,
    next_action VARCHAR(500),
    history_log TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table 3: Task Completion Record
CREATE TABLE task_completion_records (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    completed_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table 4: Recurring Task Settings
CREATE TABLE recurring_task_settings (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    size VARCHAR(20) CHECK (size IN ('UNDER_10_MIN', 'UNDER_30_MIN', 'OVER_1_HOUR')),
    recurrence_type VARCHAR(50) NOT NULL CHECK (recurrence_type IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')),
    recurrence_config JSONB,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table 5: Global Log (Undo System)
CREATE TABLE global_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL CHECK (action_type IN ('CREATE', 'UPDATE', 'DELETE')),
    old_data JSONB,
    new_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for better performance
CREATE INDEX idx_tasks_schedule_date ON tasks(schedule_date);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_work_date ON tasks(work_date);
CREATE INDEX idx_task_logs_task_id ON task_logs(task_id);
CREATE INDEX idx_task_logs_date ON task_logs(date);
CREATE INDEX idx_task_completion_records_task_id ON task_completion_records(task_id);
CREATE INDEX idx_task_completion_records_completed_date ON task_completion_records(completed_date);
CREATE INDEX idx_global_logs_entity ON global_logs(entity_type, entity_id);
CREATE INDEX idx_global_logs_created_at ON global_logs(created_at);

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers to automatically update updated_at
CREATE TRIGGER update_tasks_updated_at BEFORE UPDATE ON tasks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_task_logs_updated_at BEFORE UPDATE ON task_logs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_recurring_task_settings_updated_at BEFORE UPDATE ON recurring_task_settings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();



