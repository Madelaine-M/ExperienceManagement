package repository.implementation.mapper;

import model.domain.ActionItem;
import model.enums.ActionStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

//was implemented based on AI implemented mapper CustomerResultSetMapper
public class ActionItemResultSetMapper {

    public ActionItem map(ResultSet rs) throws SQLException {
        ActionItem actionItem = new ActionItem();
        actionItem.setId(rs.getInt("id"));
        actionItem.setIncidentId(rs.getInt("incident_id"));
        actionItem.setDescription(rs.getString("description"));
        actionItem.setSuggestion1(rs.getString("suggestion_1"));
        actionItem.setSuggestion2(rs.getString("suggestion_2"));

        String status = rs.getString("status");
        if (status != null) {
            actionItem.setStatus(ActionStatus.valueOf(status));
        }

        actionItem.setScoreImpact(rs.getInt("score_impact"));
        actionItem.setExpectedRec(rs.getInt("expected_rec"));
        actionItem.setExpectedRebooking(rs.getInt("expected_rebooking"));
        return actionItem;
    }
}
