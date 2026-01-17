package controllers;

import database.SessionDAO;
import models.DateAndTime;
import models.Seminar;
import models.Session;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SessionController {
    private SessionDAO sessionDAO = new SessionDAO();

    public void getSessionInformation(String studentId) {
        try {
            // Just printing for now as void return, but confirms DB access
            sessionDAO.findSessionsByStudent(studentId);
            System.out.println("Retrieved session info for " + studentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // CREATE SEMINAR CONTROLLER
    public void createSeminar(String seminarID, String location, int year, int month, int day, int hour, int minute){
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

    public void createSession(String sessionID, String location, int year, int month, int day, int hour, int minute, String type){
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

    public void viewSession(String sessionID){
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

    public void assignEvaluator(String sessionID){
        
    }

    public void deleteSession(String sessionID){

    }

    public void sessionSchedule(){

    }
    

    
}
