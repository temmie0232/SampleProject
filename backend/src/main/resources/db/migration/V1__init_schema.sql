CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    login_id VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE TABLE applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    applicant_user_id BIGINT NOT NULL,
    current_address VARCHAR(255) NOT NULL,
    new_address VARCHAR(255) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL,
    submitted_at DATETIME(6),
    decided_at DATETIME(6),
    decided_by_user_id BIGINT,
    rejection_reason VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_applications_applicant_user FOREIGN KEY (applicant_user_id) REFERENCES users (id),
    CONSTRAINT fk_applications_decided_by_user FOREIGN KEY (decided_by_user_id) REFERENCES users (id)
);

CREATE TABLE workflow_transition_rules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    from_status VARCHAR(20) NOT NULL,
    to_status VARCHAR(20) NOT NULL,
    actor_role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL,
    CONSTRAINT uk_workflow_transition UNIQUE (from_status, to_status, actor_role)
);

CREATE TABLE application_status_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_id BIGINT NOT NULL,
    from_status VARCHAR(20) NOT NULL,
    to_status VARCHAR(20) NOT NULL,
    acted_by_user_id BIGINT NOT NULL,
    comment VARCHAR(500),
    acted_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_app_status_history_application FOREIGN KEY (application_id) REFERENCES applications (id),
    CONSTRAINT fk_app_status_history_actor FOREIGN KEY (acted_by_user_id) REFERENCES users (id)
);

CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id VARCHAR(64) NOT NULL,
    actor_user_id BIGINT,
    action_type VARCHAR(100) NOT NULL,
    target_type VARCHAR(100) NOT NULL,
    target_id VARCHAR(100),
    result VARCHAR(20) NOT NULL,
    details_json TEXT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_audit_logs_actor FOREIGN KEY (actor_user_id) REFERENCES users (id)
);

CREATE INDEX idx_applications_status ON applications(status);
CREATE INDEX idx_applications_applicant ON applications(applicant_user_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);

INSERT INTO users (login_id, password_hash, role, display_name, enabled, created_at, updated_at)
VALUES
    ('admin01', '{noop}admin123', 'ADMIN', '管理者 太郎', TRUE, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('user01', '{noop}user123', 'USER', '一般 花子', TRUE, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO workflow_transition_rules (from_status, to_status, actor_role, active)
VALUES
    ('DRAFT', 'SUBMITTED', 'USER', TRUE),
    ('REJECTED', 'SUBMITTED', 'USER', TRUE),
    ('SUBMITTED', 'APPROVED', 'ADMIN', TRUE),
    ('SUBMITTED', 'REJECTED', 'ADMIN', TRUE);
