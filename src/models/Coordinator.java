package models;

public class Coordinator extends User{
    private String coordinatorID; // Changed from int to String for UUID

    public Coordinator(String coordinatorID, String name, String email, String password) {
        super(coordinatorID, name, password, "Coordinator");
        this.coordinatorID = coordinatorID;
    }

    public String getCoordinatorId() {
        return coordinatorID;
    }
}
