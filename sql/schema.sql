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

-- Submissions table
CREATE TABLE IF NOT EXISTS submissions (
    submission_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seminar_id VARCHAR(50) NOT NULL,
    student_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    title VARCHAR(255),
    abstract_text TEXT,
    supervisor VARCHAR(100),
    presentation_type VARCHAR(50),
    file_path VARCHAR(500),
    deadline TIMESTAMP DEFAULT (NOW() + INTERVAL '2 days'),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Sessions table
CREATE TABLE IF NOT EXISTS sessions (
    session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location VARCHAR(100),
    session_date TIMESTAMP,
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

--  Table to store the Grades you submit
CREATE TABLE IF NOT EXISTS evaluations (
    evaluation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submission_id UUID REFERENCES submissions(submission_id) ON DELETE CASCADE,
    evaluator_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    score INT,
    comments TEXT,
    problem_clarity INT,
    methodology INT,
    results INT,
    presentation INT,
    created_at TIMESTAMP DEFAULT NOW()
);
