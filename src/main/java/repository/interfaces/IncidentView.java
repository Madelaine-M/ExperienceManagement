package repository.interfaces;

import model.view.IncidentDetailView;
import model.view.IncidentOverview;

import java.util.List;

public interface IncidentView {

    List<IncidentOverview> findOpenOverviewsByAdvisorId(int advisorId);

    IncidentDetailView findDetailByIncidentId(int incidentId);

}
