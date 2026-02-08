package controllers;

import database.SessionDAO;
import database.SubmissionDAO;
import database.AssignmentDAO;
import database.UserDAO;
import models.DateAndTime;
import models.Seminar;
import models.Session;
import models.Evaluator;
import models.ScheduleItem;
import java.util.List;

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
            System.out.println("Error retrieving session info.");
        }
    }

    public void createSeminar(String seminarID, String location, int year, int month, int day, int hour, int minute,
            int semester) {
        LocalDate date = DateAndTime.dateInput(year, month, day);
        LocalTime time = DateAndTime.timeInput(hour, minute);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        DateAndTime dateAndTime = new DateAndTime(date, time);
        Seminar seminar = new Seminar(seminarID, location, dateAndTime, semester, year);
        try {
            sessionDAO.createSeminar(seminarID, location, timestamp, semester, year);
            System.out.println("Seminar created in database: " + seminarID);
        } catch (SQLException e) {
            System.out.println("Error creating seminar.");
        }
    }

    public void createSession(String sessionID, String seminarId, String location, int year, int month, int day,
            int hour, int minute,
            String type) {
        LocalDate date = DateAndTime.dateInput(year, month, day);
        LocalTime time = DateAndTime.timeInput(hour, minute);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        DateAndTime dateAndTime = new DateAndTime(date, time);
        Session session = new Session(sessionID, location, dateAndTime, type);
        try {
            sessionDAO.createSession(sessionID, seminarId, location, timestamp, type);
            System.out.println("Session created in database: " + sessionID);
        } catch (SQLException e) {
            System.out.println("Error creating session.");
        }
    }

    public void viewSession(String sessionID) {
        try {
            sessionDAO.findBySessionId(sessionID);
            System.out.println("Session: " + sessionID);
        } catch (SQLException e) {
            System.out.println("Error viewing session.");
        }
    }

    public void updateSession(String sessionID, String location, int year, int month, int day, int hour, int minute,
            String type) {
        LocalDate date = DateAndTime.dateInput(year, month, day);
        LocalTime time = DateAndTime.timeInput(hour, minute);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        DateAndTime dateAndTime = new DateAndTime(date, time);
        try {
            sessionDAO.updateSession(sessionID, location, timestamp, type);
            System.out.println("Session has been updated: " + sessionID);
        } catch (SQLException e) {
            System.out.println("Error updating session.");
        }
    }

    public void assignEvaluator(String sessionID, String evaluatorID, String studentID) {
        try {
            sessionDAO.assignEvaluator(evaluatorID, studentID);
            System.out.println("Evaluator " + evaluatorID + " assigned to student " + studentID);
        } catch (SQLException e) {
            System.out.println("Error assigning evaluator: Database error occurred.");
        }
    }

    public void deleteSession(String sessionID) {
        try {
            sessionDAO.deleteSession(sessionID);
            System.out.println("Session " + sessionID + "has been deleted");
        } catch (SQLException e) {
            System.out.println("Error deleting the session: Database error occurred.");
        }
    }

    public void sessionSchedule() {
        List<ScheduleItem> schedule = sessionDAO.getSessionSchedule();
        System.out.println("Session Schedule:");
        System.out.println("-----------------");
        for (ScheduleItem item : schedule) {
            System.out.println(item.toString());
        }
    }

}
