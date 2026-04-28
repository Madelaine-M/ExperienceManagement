package ui.service;

import ui.model.JourneyDetailData;

public interface JourneyDetailService {
    JourneyDetailData loadJourneyDetail(int customerId, Integer incidentId);

    JourneyDetailData saveAdvisorNote(int customerId, Integer incidentId, int advisorId, String noteText);
}
