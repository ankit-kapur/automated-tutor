package tutorapp;

public class TutorApplication {

    public static void main(String args[]) {

        Controller tutorController = new Controller();
        
        tutorController.loadConfiguration();
        tutorController.refreshTutor();
    }
}
