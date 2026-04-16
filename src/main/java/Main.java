import database.initialization.DataSeeder;
import service.Backend;

public class Main {
    public static void main(String[] args) {
        Backend backend = new Backend();

        // 3. Testdaten generieren (nur beim ersten Mal)
        DataSeeder.seed(
                backend.getAdvisorRepository(),
                backend.getCustomerLookup(),
                backend.getCustomerUpdate(),
                backend.getFlightRepository(),
                backend.getFeedbackUpdate(),
                backend.getIncidentLookup(),
                backend.getIncidentUpdate(),
                backend.getActionLookup(),
                backend.getActionUpdate()
        );

        // 2. Danach erst Services oder UI starten
        System.out.println("Ready!");
    }
}
