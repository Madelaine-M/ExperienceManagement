package database.initialization;

import model.ActionItem;
import model.Advisor;
import model.Customer;
import model.Incident;
import model.enums.ActionStatus;
import model.enums.CustomerStatus;
import model.enums.IncidentStatus;
import model.enums.IncidentType;
import model.enums.Packages;
import repository.interfaces.ActionLookup;
import repository.interfaces.ActionUpdate;
import repository.interfaces.AdvisorRepository;
import repository.interfaces.CustomerLookup;
import repository.interfaces.CustomerUpdate;
import repository.interfaces.IncidentLookup;
import repository.interfaces.IncidentUpdate;

public class DataSeeder {

    public static void seed(
            AdvisorRepository advisorRepo,
            CustomerLookup customerLookup,
            CustomerUpdate customerUpdate,
            IncidentLookup incidentLookup,
            IncidentUpdate incidentUpdate,
            ActionLookup actionLookup,
            ActionUpdate actionUpdate
    ) {
        System.out.println("Seeding initial data...");

        if (advisorRepo.findAll().isEmpty()) {
            Advisor a1 = new Advisor("Anna", "Schmidt", "anna.schmidt@test.de", "Premium Support", 42.5);
            advisorRepo.save(a1);

            Advisor a2 = new Advisor("Lukas", "Weber", "lukas.weber@test.de", "Flight Recovery", 27.0);
            advisorRepo.save(a2);
        }

        var allAdvisors = advisorRepo.findAll();
        int annaId = allAdvisors.get(0).getId();
        int lukasId = allAdvisors.get(1).getId();

        if (customerLookup.findAll().isEmpty()) {
            Customer c1 = new Customer("Max", "Mustermann", "max@test.de", CustomerStatus.BOOKED);
            c1.setClvScore(1500.50f);
            c1.setBookingPackage(Packages.VIP);
            c1.setReturning(true);
            c1.setAssignedAdvisorId(annaId);
            customerUpdate.save(c1);

            Customer c2 = new Customer("Erika", "Musterfrau", "erika@web.de", CustomerStatus.BOOKED);
            c2.setClvScore(5000.00f);
            c2.setBookingPackage(Packages.VIP);
            c2.setReturning(false);
            c2.setAssignedAdvisorId(lukasId);
            customerUpdate.save(c2);
        }

        var allCustomers = customerLookup.findAll();
        int maxId = allCustomers.get(0).getId();
        int erikaId = allCustomers.size() > 1 ? allCustomers.get(1).getId() : maxId;

        if (incidentLookup.findAllByCustomerId(maxId).isEmpty()) {
            Incident i1 = new Incident();
            i1.setCustomerId(maxId);
            i1.setType(IncidentType.DELAY);
            i1.setDescription("Flight LH123 has a 4 hour delay.");
            i1.setPriorityScore(8.5);
            i1.setScoreImpact(-1.2);
            i1.setStatus(IncidentStatus.OPEN);
            i1.setAssignedAdvisorId(annaId);
            incidentUpdate.save(i1);
        }

        if (incidentLookup.findAllByCustomerId(erikaId).isEmpty()) {
            Incident i2 = new Incident();
            i2.setCustomerId(erikaId);
            i2.setType(IncidentType.FEEDBACK);
            i2.setDescription("Food was great, but seat was bad.");
            i2.setPriorityScore(3.0);
            i2.setScoreImpact(-0.4);
            i2.setStatus(IncidentStatus.CLOSED);
            incidentUpdate.save(i2);
        }

        int firstIncidentId = incidentLookup.findAllByCustomerId(maxId).get(0).getId();

        if (actionLookup.findByIncidentId(firstIncidentId).isEmpty()) {
            ActionItem actionItem1 = new ActionItem(firstIncidentId, "Offer lounge voucher and proactive delay updates.", ActionStatus.SUGGESTED, 9);
            actionUpdate.save(actionItem1);

            ActionItem actionItem2 = new ActionItem(firstIncidentId, "Rebook to the next available direct flight.", ActionStatus.SUGGESTED, 10);
            actionUpdate.save(actionItem2);
        }

        System.out.println("Seeding complete! Advisors, customers, incidents and action items created.");
    }
}
