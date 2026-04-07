import database.initialization.DataSeeder;
import service.Backend;

public class Main {
    public static void main(String[] args) {
        Backend backend = new Backend();

        // 3. Testdaten generieren (nur beim ersten Mal)
        DataSeeder.seed(
                backend.getAdvisorRepository(),
                backend.getCustomerRepository(),
                backend.getIncidentRepository(),
                backend.getActionRepository(),
                backend.getHistoryRepository()
        );

        // 2. Danach erst Services oder UI starten
        System.out.println("Ready!");
    }
}
