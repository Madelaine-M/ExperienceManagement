package service.calculation;

import model.Incident;
import model.enums.IncidentType;
import org.jetbrains.annotations.NotNull;
import service.interfaces.CustomerService;

public class PriorityCalculator {
    private Incident  incident;
    private CustomerService customerService;
    private ExpectedImpactCalculatorService expectedImpactCalculatorService;
    public PriorityCalculator(Incident incident) {
        this.incident = incident;
    }
    public double calculate(@NotNull Incident incident) {
        return (getBaseSeverity(incident.getType())*
                getCVPart(incident.getCustomerId())+
                (expectedImpactCalculatorService.calculateDefaultScoreImpact(incident.getType())*10)+
                (expectedImpactCalculatorService.calculateRevenueImpact()/2000)
        );
    }

    private double getBaseSeverity(IncidentType type) { //SOLID Zweifel
        if (type == IncidentType.DELAY){
            return 1.3;
        }
        else  {
            return 1.5;
        }
    }

    private double getCVPart(int customerId){

        return (1+ (customerService.findById(customerId)).getClvScore()/100);
    }


}
