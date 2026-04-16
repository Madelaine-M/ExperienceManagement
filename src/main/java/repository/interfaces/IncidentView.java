package repository.interfaces;

import model.IncidentDetailView;
import model.IncidentOverview;

import java.util.List;

public interface IncidentView {

    List<IncidentOverview> findAllPrioritizedOverviews();

    IncidentDetailView findDetailByIncidentId(int incidentId);

}
