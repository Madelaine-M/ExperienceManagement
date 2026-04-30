package ui.service;

import model.view.CustomerDetailView;
import model.domain.CustomerNote;
import model.domain.Feedback;
import model.domain.FeedbackItem;
import model.view.IncidentDetailView;
import model.enums.CustomerStatus;
import service.interfaces.frontend.CustomerNoteService;
import service.interfaces.frontend.CustomerService;
import service.interfaces.frontend.FeedbackService;
import service.interfaces.frontend.IncidentService;
import ui.model.JourneyDetailData;
import ui.model.JourneyStepState;
import ui.model.JourneyStepView;
import ui.view.DashboardFormatters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JourneyDetailServiceImpl implements JourneyDetailService {
    private final CustomerService customerService;
    private final IncidentService incidentService;
    private final FeedbackService feedbackService;
    private final CustomerNoteService customerNoteService;

    public JourneyDetailServiceImpl(CustomerService customerService,
                                    IncidentService incidentService,
                                    FeedbackService feedbackService,
                                    CustomerNoteService customerNoteService) {
        this.customerService = customerService;
        this.incidentService = incidentService;
        this.feedbackService = feedbackService;
        this.customerNoteService = customerNoteService;
    }

    @Override
    public JourneyDetailData loadJourneyDetail(int customerId, Integer incidentId) {
        CustomerDetailView customerDetail = customerService.findDetailByCustomerId(customerId);
        if (customerDetail == null) {
            return null;
        }

        IncidentDetailView incidentDetail = incidentId == null ? null : incidentService.findDetailByIncidentId(incidentId);
        Feedback feedback = loadFeedback(incidentDetail);
        FeedbackItem highlightedFeedbackItem = findHighlightedFeedbackItem(feedback, incidentDetail);

        return new JourneyDetailData(
                customerId,
                incidentId,
                DashboardFormatters.formatName(customerDetail.getCustomerFirstName(), customerDetail.getCustomerLastName()),
                customerDetail,
                incidentDetail,
                feedback,
                highlightedFeedbackItem,
                buildJourneySteps(customerDetail.getStatus())
        );
    }

    @Override
    public JourneyDetailData saveAdvisorNote(int customerId, Integer incidentId, int advisorId, String noteText) {
        String sanitized = noteText == null ? "" : noteText.trim();
        if (!sanitized.isBlank()) {
            LocalDateTime now = LocalDateTime.now();
            customerNoteService.save(new CustomerNote(0, customerId, advisorId, sanitized, now, now));
        }
        return loadJourneyDetail(customerId, incidentId);
    }

    private Feedback loadFeedback(IncidentDetailView incidentDetail) {
        if (incidentDetail == null || incidentDetail.getFeedbackId() == null) {
            return null;
        }
        return feedbackService.findById(incidentDetail.getFeedbackId());
    }

    private FeedbackItem findHighlightedFeedbackItem(Feedback feedback, IncidentDetailView incidentDetail) {
        if (feedback == null || incidentDetail == null || incidentDetail.getSourceFeedbackItemId() == null) {
            return null;
        }

        for (FeedbackItem item : feedback.getItems()) {
            if (item.getId() == incidentDetail.getSourceFeedbackItemId()) {
                return item;
            }
        }
        return null;
    }

    private List<JourneyStepView> buildJourneySteps(CustomerStatus currentStatus) {
        List<JourneyStepView> steps = new ArrayList<>();
        CustomerStatus[] statuses = CustomerStatus.values();
        int currentIndex = currentStatus == null ? -1 : currentStatus.ordinal();

        for (int index = 0; index < statuses.length; index++) {
            JourneyStepState state;
            if (currentIndex < 0 || index > currentIndex) {
                state = JourneyStepState.UPCOMING;
            } else if (index == currentIndex) {
                state = JourneyStepState.CURRENT;
            } else {
                state = JourneyStepState.COMPLETED;
            }
            steps.add(new JourneyStepView(formatStatus(statuses[index]), state));
        }

        return steps;
    }

    private String formatStatus(CustomerStatus status) {
        String[] words = status.name().split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(word.charAt(0)).append(word.substring(1).toLowerCase());
        }
        return builder.toString();
    }
}
