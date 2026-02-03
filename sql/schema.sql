-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'student',
    created_at TIMESTAMP DEFAULT NOW()
);

-- Student Profiles table
CREATE TABLE IF NOT EXISTS student_profiles (
    profile_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    full_name VARCHAR(100),
    email VARCHAR(100),
    major VARCHAR(100),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Seminars table (must be created before submissions which references it)
CREATE TABLE IF NOT EXISTS Seminars (
    seminar_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location VARCHAR(100),
    seminar_date TIMESTAMP,
    semester INT DEFAULT 1,
    year INT DEFAULT 2026,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Submissions table (FIXED: status column added here)
CREATE TABLE IF NOT EXISTS submissions (
    submission_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seminar_id UUID REFERENCES Seminars(seminar_id) ON DELETE CASCADE,
    student_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    title VARCHAR(255),
    abstract_text TEXT,
    supervisor VARCHAR(100),
    presentation_type VARCHAR(50),
    file_path VARCHAR(500),
    status VARCHAR(50) DEFAULT 'SUBMITTED',
    deadline TIMESTAMP DEFAULT (NOW() + INTERVAL '2 days'),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Materials table
CREATE TABLE IF NOT EXISTS materials (
    material_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submission_id UUID REFERENCES submissions(submission_id) ON DELETE CASCADE,
    file_name VARCHAR(255),
    file_type VARCHAR(50),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    file_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Sessions table
CREATE TABLE IF NOT EXISTS sessions (
    session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seminar_id UUID REFERENCES Seminars(seminar_id) ON DELETE CASCADE,
    location VARCHAR(100),
    session_date TIMESTAMP,
    session_type VARCHAR(50) DEFAULT 'Oral Presentation',
    evaluator_id UUID REFERENCES users(user_id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Session-Students junction table (many-to-many)
CREATE TABLE IF NOT EXISTS session_students (
    session_id UUID REFERENCES sessions(session_id) ON DELETE CASCADE,
    student_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (session_id, student_id)
);

-- Table to link an Evaluator to a Submission 
CREATE TABLE IF NOT EXISTS evaluator_assignments (
    assignment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evaluator_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    submission_id UUID REFERENCES submissions(submission_id) ON DELETE CASCADE
);

-- Table to store the Grades you submit
CREATE TABLE IF NOT EXISTS evaluations (
    evaluation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submission_id UUID REFERENCES submissions(submission_id) ON DELETE CASCADE,
    evaluator_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    comments TEXT,
    problem_clarity INT,
    methodology INT,
    results INT,
    presentation INT,
    overall_score INT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Presentation Boards Table
CREATE TABLE IF NOT EXISTS presentation_boards (
    board_id SERIAL PRIMARY KEY,
    board_name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    max_presentations INT NOT NULL,
    current_presentations INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Poster Presentations Table
CREATE TABLE IF NOT EXISTS poster_presentations (
    presentation_id SERIAL PRIMARY KEY,
    board_id INT NOT NULL,
    submission_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    session_id UUID NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES presentation_boards(board_id) ON DELETE CASCADE,
    FOREIGN KEY (submission_id) REFERENCES submissions(submission_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE CASCADE
);

-- Evaluation Criteria Table
CREATE TABLE IF NOT EXISTS evaluation_criteria (
    criteria_id SERIAL PRIMARY KEY,
    presentation_id INT NOT NULL,
    criteria_name VARCHAR(255) NOT NULL,
    description TEXT,
    max_score INT NOT NULL,
    weight INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (presentation_id) REFERENCES poster_presentations(presentation_id) ON DELETE CASCADE
);

-- Schedule table to store session schedules
CREATE TABLE IF NOT EXISTS schedule (
    schedule_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID REFERENCES sessions(session_id) ON DELETE CASCADE,
    student_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    evaluator_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    scheduled_date DATE,
    scheduled_time TIME,
    venue VARCHAR(100),
    created_at TIMESTAMP DEFAULT NOW()
);
