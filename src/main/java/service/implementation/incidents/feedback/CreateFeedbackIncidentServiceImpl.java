package service.implementation.incidents.feedback;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import model.domain.Customer;
import model.domain.Feedback;
import model.domain.FeedbackIncident;
import model.domain.FeedbackItem;
import model.enums.IncidentStatus;
import model.enums.IncidentType;
import repository.interfaces.CustomerLookup;
import repository.interfaces.FeedbackLookup;
import repository.interfaces.IncidentLookup;
import repository.interfaces.IncidentUpdate;
import service.interfaces.internal.CreateFeedbackIncidentService;
import service.interfaces.internal.ExpectedImpactCalcService;
import service.interfaces.internal.PriorityCalcService;

public class CreateFeedbackIncidentServiceImpl implements CreateFeedbackIncidentService {
    private final IncidentUpdate incidentUpdate;
    private final IncidentLookup incidentLookup;
    private final FeedbackLookup feedbackLookup;
    private final CustomerLookup customerLookup;
    private final ExpectedImpactCalcService expectedImpactCalcService;
    private final PriorityCalcService priorityCalcService;

    public CreateFeedbackIncidentServiceImpl(IncidentUpdate incidentUpdate,
                                             IncidentLookup incidentLookup,
                                             FeedbackLookup feedbackLookup,
                                             CustomerLookup customerLookup,
                                             ExpectedImpactCalcService expectedImpactCalcService,
                                             PriorityCalcService priorityCalcService) {
        this.incidentUpdate = incidentUpdate;
        this.incidentLookup = incidentLookup;
        this.feedbackLookup = feedbackLookup;
        this.customerLookup = customerLookup;
        this.expectedImpactCalcService = expectedImpactCalcService;
        this.priorityCalcService = priorityCalcService;
    }

    @Override
    public List<FeedbackIncident> createFeedbackIncident(List<FeedbackItem> feedbackItems) {
        List<FeedbackIncident> createdIncidents = new ArrayList<>();
        if (feedbackItems == null || feedbackItems.isEmpty()) {
            return createdIncidents;
        }

        for (FeedbackItem feedbackItem : selectWorstItemPerFeedback(feedbackItems).values()) {
            Feedback feedback = feedbackLookup.findById(feedbackItem.getFeedbackId());
            if (feedback == null) {
                continue;
            }

            if (incidentLookup.existsByFeedbackId(feedback.getId())
                    || incidentLookup.existsBySourceFeedbackItemId(feedbackItem.getId())) {
                continue;
            }

            Customer customer = customerLookup.findById(feedback.getCustomerId());
            if (customer == null) {
                continue;
            }

            FeedbackIncident feedbackIncident = new FeedbackIncident();
            feedbackIncident.setCustomerId(customer.getId());
            feedbackIncident.setAssignedAdvisorId(customer.getAssignedAdvisorId());
            feedbackIncident.setFeedbackId(feedback.getId());
            feedbackIncident.setDescription(buildDescription(feedbackItem));
            feedbackIncident.setSourceFeedbackItemId(feedbackItem.getId());
            feedbackIncident.setFeedbackType(feedbackItem.getCategory());
            feedbackIncident.setCreatedAt(java.time.LocalDateTime.now());
            feedbackIncident.setFlightId(feedback.getFlightId());
            feedbackIncident.setStatus(IncidentStatus.OPEN);

            int scoreImpact = expectedImpactCalcService.calculateDefaultScoreImpact(IncidentType.FEEDBACK);
            feedbackIncident.setScoreImpact(scoreImpact);
            feedbackIncident.setRevenueRisk(expectedImpactCalcService.calculateRevenueImpact(scoreImpact));
            incidentUpdate.save(feedbackIncident);
            createdIncidents.add(feedbackIncident);
        }
        return createdIncidents;
    }

    // AI used
    private Map<Integer, FeedbackItem> selectWorstItemPerFeedback(List<FeedbackItem> feedbackItems) {
        Map<Integer, FeedbackItem> worstByFeedbackId = new LinkedHashMap<>();

        for (FeedbackItem feedbackItem : feedbackItems) {
            if (feedbackItem == null || feedbackItem.getFeedbackId() <= 0) {
                continue;
            }

            FeedbackItem currentWorst = worstByFeedbackId.get(feedbackItem.getFeedbackId());
            if (currentWorst == null || compareSeverity(feedbackItem, currentWorst) < 0) {
                worstByFeedbackId.put(feedbackItem.getFeedbackId(), feedbackItem);
            }
        }

        return worstByFeedbackId;
    }

    private int compareSeverity(FeedbackItem left, FeedbackItem right) {
        return Comparator
                .comparingInt(FeedbackItem::getScore)
                .thenComparingInt(FeedbackItem::getId)
                .compare(left, right);
    }

    private String buildDescription(FeedbackItem feedbackItem) {
        String comment = feedbackItem.getComment();
        if (comment != null && !comment.isBlank()) {
            return comment;
        }
        return "Low feedback score in category " + feedbackItem.getCategory();
    }
}
