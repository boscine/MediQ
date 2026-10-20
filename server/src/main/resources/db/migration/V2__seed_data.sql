-- Seed specializations
INSERT INTO specializations (id, name, description) VALUES
('cardiology', 'Cardiology', 'Heart and cardiovascular system'),
('dermatology', 'Dermatology', 'Skin, hair, and nails'),
('pediatrics', 'Pediatrics', 'Children''s health'),
('orthopedics', 'Orthopedics', 'Bones, joints, and muscles'),
('neurology', 'Neurology', 'Nervous system disorders'),
('psychiatry', 'Psychiatry', 'Mental health');

-- Seed admin user (password: admin12345)
INSERT INTO users (id, username, password_hash, full_name, email, mobile_number, date_of_birth, role, status) VALUES
('admin-1', 'admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J8.5Y9Qn.', 'Admin User', 'admin@mediq.app', '+639170000000', '1985-01-01', 'admin', 'active');

-- Seed doctors
INSERT INTO users (id, username, password_hash, full_name, email, mobile_number, date_of_birth, role, status) VALUES
('doc-1', 'dr.smith', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J8.5Y9Qn.', 'Dr. Sarah Smith', 'dr.smith@mediq.app', '+639171234567', '1980-05-15', 'doctor', 'active'),
('doc-2', 'dr.chen', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J8.5Y9Qn.', 'Dr. Michael Chen', 'dr.chen@mediq.app', '+639172345678', '1975-11-22', 'doctor', 'active'),
('doc-3', 'dr.garcia', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J8.5Y9Qn.', 'Dr. Maria Garcia', 'dr.garcia@mediq.app', '+639173456789', '1982-08-30', 'doctor', 'active');

INSERT INTO doctors (id, user_id, full_name, specialty, specialization_id, license_number, years_of_experience, fee_centavos, bio, building, floor, room, languages, status) VALUES
('doc-1', 'doc-1', 'Dr. Sarah Smith', 'Cardiology', 'cardiology', 'PHYS-12345', 15, 150000, 'Board-certified cardiologist with 15 years experience.', 'MediQ Medical Center', '3', '301', '["en", "tl"]', 'active'),
('doc-2', 'doc-2', 'Dr. Michael Chen', 'Dermatology', 'dermatology', 'PHYS-23456', 12, 120000, 'Dermatologist specializing in medical and cosmetic dermatology.', 'MediQ Medical Center', '2', '205', '["en", "zh"]', 'active'),
('doc-3', 'doc-3', 'Dr. Maria Garcia', 'Pediatrics', 'pediatrics', 'PHYS-34567', 10, 100000, 'Pediatrician with focus on developmental pediatrics.', 'MediQ Medical Center', '1', '110', '["en", "tl", "es"]', 'active');

-- Update users with doctor_id
UPDATE users SET doctor_id = 'doc-1' WHERE id = 'doc-1';
UPDATE users SET doctor_id = 'doc-2' WHERE id = 'doc-2';
UPDATE users SET doctor_id = 'doc-3' WHERE id = 'doc-3';

-- Seed secretary
INSERT INTO users (id, username, password_hash, full_name, email, mobile_number, date_of_birth, role, status, assigned_doctor_id) VALUES
('sec-1', 'sec.jones', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J8.5Y9Qn.', 'Secretary Jane Jones', 'sec.jones@mediq.app', '+639173456789', '1990-03-10', 'secretary', 'active', 'doc-1');

-- Seed patients
INSERT INTO users (id, username, password_hash, full_name, email, mobile_number, date_of_birth, role, status) VALUES
('pat-1', 'patient1', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J8.5Y9Qn.', 'John Patient', 'john@patient.com', '+639181111111', '1995-07-20', 'patient', 'active'),
('pat-2', 'patient2', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J8.5Y9Qn.', 'Maria Santos', 'maria@patient.com', '+639182222222', '1988-12-05', 'patient', 'active');

-- Seed clinic schedules (Mon-Fri 9-5 for all doctors)
INSERT INTO clinic_schedules (id, doctor_id, day_of_week, start_time, end_time, is_closed) VALUES
('sched-doc1-0', 'doc-1', 0, '09:00', '17:00', true),
('sched-doc1-1', 'doc-1', 1, '09:00', '17:00', false),
('sched-doc1-2', 'doc-1', 2, '09:00', '17:00', false),
('sched-doc1-3', 'doc-1', 3, '09:00', '17:00', false),
('sched-doc1-4', 'doc-1', 4, '09:00', '17:00', false),
('sched-doc1-5', 'doc-1', 5, '09:00', '17:00', false),
('sched-doc1-6', 'doc-1', 6, '09:00', '17:00', true),
('sched-doc2-0', 'doc-2', 0, '09:00', '17:00', true),
('sched-doc2-1', 'doc-2', 1, '09:00', '17:00', false),
('sched-doc2-2', 'doc-2', 2, '09:00', '17:00', false),
('sched-doc2-3', 'doc-2', 3, '09:00', '17:00', false),
('sched-doc2-4', 'doc-2', 4, '09:00', '17:00', false),
('sched-doc2-5', 'doc-2', 5, '09:00', '17:00', false),
('sched-doc2-6', 'doc-2', 6, '09:00', '17:00', true),
('sched-doc3-0', 'doc-3', 0, '09:00', '17:00', true),
('sched-doc3-1', 'doc-3', 1, '09:00', '17:00', false),
('sched-doc3-2', 'doc-3', 2, '09:00', '17:00', false),
('sched-doc3-3', 'doc-3', 3, '09:00', '17:00', false),
('sched-doc3-4', 'doc-3', 4, '09:00', '17:00', false),
('sched-doc3-5', 'doc-3', 5, '09:00', '17:00', false),
('sched-doc3-6', 'doc-3', 6, '09:00', '17:00', true);

-- Seed some appointments
INSERT INTO appointments (id, patient_id, doctor_id, starts_at, duration_minutes, status, reason) VALUES
('apt-1', 'pat-1', 'doc-1', CURRENT_TIMESTAMP + INTERVAL '1' DAY, 30, 'booked', 'Regular checkup'),
('apt-2', 'pat-2', 'doc-1', CURRENT_TIMESTAMP + INTERVAL '1' DAY + INTERVAL '1' HOUR, 30, 'confirmed', 'Follow-up visit'),
('apt-3', 'pat-1', 'doc-2', CURRENT_TIMESTAMP + INTERVAL '2' DAY, 45, 'booked', 'Skin consultation');