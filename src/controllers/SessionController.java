package controllers;

import database.SessionDAO;
import database.SubmissionDAO;
import database.AssignmentDAO;
import database.UserDAO;
import models.DateAndTime;
import models.Seminar;
import models.Session;
import models.Evaluator;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SessionController {
    private SessionDAO sessionDAO = new SessionDAO();

    public void getSessionInformation(String studentId) {
        try {

            sessionDAO.findSessionsByStudent(studentId);
            System.out.println("Retrieved session info for " + studentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createSeminar(String seminarID, String location, int year, int month, int day, int hour, int minute) {
        LocalDate date = DateAndTime.dateInput(year, month, day);
        LocalTime time = DateAndTime.timeInput(hour, minute);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        DateAndTime dateAndTime = new DateAndTime(date, time);
        Seminar seminar = new Seminar(seminarID, location, dateAndTime);
        try {
            sessionDAO.createSeminar(seminarID, location, timestamp);
            System.out.println("Seminar created in database: " + seminarID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createSession(String sessionID, String location, int year, int month, int day, int hour, int minute,
            String type) {
        LocalDate date = DateAndTime.dateInput(year, month, day);
        LocalTime time = DateAndTime.timeInput(hour, minute);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        DateAndTime dateAndTime = new DateAndTime(date, time);
        Session session = new Session(sessionID, location, dateAndTime, type);
        try {
            sessionDAO.createSession(sessionID, location, timestamp, type);
            System.out.println("Session created in database: " + sessionID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewSession(String sessionID) {
        try {
            sessionDAO.findBySessionId(sessionID);
            System.out.println("Session: " + sessionID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSession(String sessionID, String location, int year, int month, int day, int hour, int minute, String type){
        try {
            sessionDAO.updateSession(sessionID, );
            System.out.println("Session: " + sessionID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void assignEvaluator(String sessionID, String evaluatorID, String studentID) {
        try {
            // check evaluator assigned or not
            AssignmentDAO assignmentDAO = new AssignmentDAO();
            if (assignmentDAO.getAssignmentsForEvaluator(evaluatorID).isEmpty()) {
                // find submission by that student
                SubmissionDAO submissionDAO = new SubmissionDAO();
                ResultSet rs = submissionDAO.findByStudentId(studentID);
                if (rs.next()) {
                    String submissionId = rs.getString("submission_id");
                    // assign evaluator to the student's submission
                    String sql = "INSERT INTO evaluator_assignments (evaluator_id, submission_id) VALUES (?::uuid, ?::uuid)";
                    java.sql.Connection conn = database.DatabaseConnection.getConnection();
                    java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, evaluatorID);
                    stmt.setString(2, submissionId);
                    stmt.executeUpdate();
                    stmt.close();
                    System.out.println("Evaluator " + evaluatorID + " assigned to student " + studentID);
                } else {
                    System.out.println("No submission found for student " + studentID);
                }
                rs.close();
            } else {
                System.out.println("Evaluator " + evaluatorID + " is already assigned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSession(String sessionID) {

    }

    public void sessionSchedule() {

    }

}
