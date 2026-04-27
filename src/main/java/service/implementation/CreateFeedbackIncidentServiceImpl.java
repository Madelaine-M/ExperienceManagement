package service.implementation;

import java.util.List;

import model.enums.IncidentStatus;
import model.enums.IncidentType;
import org.jetbrains.annotations.NotNull;

import model.FeedbackIncident;
import model.FeedbackItem;
import repository.interfaces.FeedbackLookup;
import repository.interfaces.IncidentLookup;
import repository.interfaces.IncidentUpdate;
import service.interfaces.internal.CreateFeedbackIncidentService;
import service.interfaces.internal.ExpectedImpactCalcService;
import service.interfaces.internal.PriorityCalcService;


public class CreateFeedbackIncidentServiceImpl implements CreateFeedbackIncidentService {
private final IncidentUpdate incidentUpdate;
private final FeedbackLookup feedbackLookup;
private final ExpectedImpactCalcService expectedImpactCalcService;
private final PriorityCalcService priorityCalcService;

    public CreateFeedbackIncidentServiceImpl(IncidentUpdate incidentUpdate, IncidentLookup incidentLookup, FeedbackLookup feedbackLookup, ExpectedImpactCalcService expectedImpactCalcService, PriorityCalcService priorityCalcService) {
        this.incidentUpdate = incidentUpdate;
        this.feedbackLookup = feedbackLookup;
        this.expectedImpactCalcService = expectedImpactCalcService;
        this.priorityCalcService = priorityCalcService;
    }

    @Override
    public void createFeedbackIncident(@NotNull List<FeedbackItem> feedbackItems) {
        int i=0;
        for (FeedbackItem feedbackItem : feedbackItems) {
            FeedbackIncident feedbackIncident = new FeedbackIncident();
            feedbackIncident.setId(i);
            feedbackIncident.setCustomerId((feedbackLookup.findById(feedbackItem.getFeedbackId())).getCustomerId());
            feedbackIncident.setDescription(feedbackItem.getComment());
            feedbackIncident.setSourceFeedbackItemId(feedbackItem.getId());
            feedbackIncident.setFeedbackType(feedbackItem.getCategory());
            // Set the createdAt timestamp to the current time in LocalDateTime format
            feedbackIncident.setCreatedAt(java.time.LocalDateTime.now());
            feedbackIncident.setFlightId((feedbackLookup.findById(feedbackItem.getFeedbackId())).getFlightId());
            feedbackIncident.setRevenueRisk(expectedImpactCalcService.calculateRevenueImpact());
            feedbackIncident.setStatus(IncidentStatus.OPEN);
            feedbackIncident.setScoreImpact(expectedImpactCalcService.calculateDefaultScoreImpact(IncidentType.FEEDBACK));
            feedbackIncident.setPriorityScore(priorityCalcService.calculate(feedbackIncident));
            incidentUpdate.save(feedbackIncident);
        }
    }
}
