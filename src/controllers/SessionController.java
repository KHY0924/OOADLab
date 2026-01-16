package controllers;

import database.SessionDAO;
import java.sql.SQLException;

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

    public class DateAndTime {
        public static void dateInput(int year, int month, int day){
            LocalDate date = LocalDate.of(year, month, day);
        }
    
        public static void timeInput(int hour, int minute){
            LocalTime time = LocalTime.of(hour, minute);
        }
    }
    
    
}















