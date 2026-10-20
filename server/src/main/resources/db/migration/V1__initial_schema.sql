-- Create users table
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    mobile_number VARCHAR(20) NOT NULL,
    date_of_birth DATE NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    doctor_id VARCHAR(36),
    assigned_doctor_id VARCHAR(36),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_doctor_id ON users(doctor_id);

-- Create doctors table
CREATE TABLE doctors (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL REFERENCES users(id),
    full_name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    specialization_id VARCHAR(50) NOT NULL,
    license_number VARCHAR(50) NOT NULL,
    years_of_experience INT NOT NULL,
    fee_centavos BIGINT NOT NULL,
    bio TEXT,
    building VARCHAR(100),
    floor VARCHAR(20),
    room VARCHAR(20),
    languages JSON,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_doctors_user_id ON doctors(user_id);
CREATE INDEX idx_doctors_specialization ON doctors(specialization_id);

-- Create specializations table
CREATE TABLE specializations (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create appointments table
CREATE TABLE appointments (
    id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL REFERENCES users(id),
    doctor_id VARCHAR(36) NOT NULL REFERENCES doctors(id),
    starts_at TIMESTAMP NOT NULL,
    duration_minutes INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'booked',
    reason TEXT,
    outcome VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_starts_at ON appointments(starts_at);
CREATE INDEX idx_appointments_status ON appointments(status);

-- Create clinic schedules table
CREATE TABLE clinic_schedules (
    id VARCHAR(36) PRIMARY KEY,
    doctor_id VARCHAR(36) NOT NULL REFERENCES doctors(id),
    day_of_week INT NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    start_time VARCHAR(5) NOT NULL,
    end_time VARCHAR(5) NOT NULL,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(doctor_id, day_of_week)
);

-- Create available dates table (dated overrides)
CREATE TABLE available_dates (
    id VARCHAR(36) PRIMARY KEY,
    doctor_id VARCHAR(36) NOT NULL REFERENCES doctors(id),
    date DATE NOT NULL,
    sessions JSON NOT NULL,
    is_override BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE(doctor_id, date)
);

-- Create conversations table
CREATE TABLE conversations (
    id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL REFERENCES users(id),
    doctor_id VARCHAR(36) NOT NULL REFERENCES doctors(id),
    subject VARCHAR(255) NOT NULL,
    last_message_at TIMESTAMP NOT NULL,
    unread_count INT NOT NULL DEFAULT 0,
    awaiting_action BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_conversations_patient ON conversations(patient_id);
CREATE INDEX idx_conversations_doctor ON conversations(doctor_id);

-- Create messages table
CREATE TABLE messages (
    id VARCHAR(36) PRIMARY KEY,
    conversation_id VARCHAR(36) NOT NULL REFERENCES conversations(id),
    author_id VARCHAR(36) NOT NULL REFERENCES users(id),
    author_role VARCHAR(20) NOT NULL,
    body TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    read_at TIMESTAMP
);

CREATE INDEX idx_messages_conversation ON messages(conversation_id);
CREATE INDEX idx_messages_sent_at ON messages(sent_at);

-- Create notifications table
CREATE TABLE notifications (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL REFERENCES users(id),
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    data JSON,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP
);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);