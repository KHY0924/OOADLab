package controllers;

import models.Session;
import java.util.List;

public class SessionController {

    public void getSessionInformation(String studentId) {
        List<Session> sessionList = Session.findSessionsByStudent(studentId);
    }
}
