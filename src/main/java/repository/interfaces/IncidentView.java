package repository.interfaces;

import model.IncidentDetailView;
import model.IncidentOverview;

import java.util.List;

public interface IncidentView {

    List<IncidentOverview> findAllPrioritizedOverviews(int advisorId);

    IncidentDetailView findDetailByIncidentId(int incidentId);

}
