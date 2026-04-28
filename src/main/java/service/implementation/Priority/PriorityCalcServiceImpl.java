package service.implementation.Priority;

import model.Incident;
import model.enums.IncidentType;
import service.interfaces.frontend.CustomerService;
import service.interfaces.internal.ExpectedImpactCalcService;

public class PriorityCalcServiceImpl {
    private Incident  incident;
    private CustomerService customerService;
    private ExpectedImpactCalcService expectedImpactCalcService;
    public PriorityCalcServiceImpl(Incident incident) {
        this.incident = incident;
    }
    public double calculate(Incident incident) {
        return (getBaseSeverity(incident.getType())*
                getCVPart(incident.getCustomerId())+
                (expectedImpactCalcService.calculateDefaultScoreImpact(incident.getType())*10)+
                (expectedImpactCalcService.calculateRevenueImpact()/2000)
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

        return (1+ (customerService.findById(customerId)).getCvScore()/100);
    }


}
