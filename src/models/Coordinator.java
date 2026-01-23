package models;

public class Coordinator extends User {
    private String coordinatorID;

    public Coordinator(String coordinatorID, String name, String email, String password) {
        super(coordinatorID, name, password, "Coordinator");
        this.coordinatorID = coordinatorID;
    }

    public String getCoordinatorId() {
        return coordinatorID;
    }
}

