package repository.implementation.mapper;

import model.domain.FeedbackItem;
import model.enums.FeedbackCategory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedbackItemResultSetMapper {

    public FeedbackItem map(ResultSet rs) throws SQLException {
        FeedbackItem item = new FeedbackItem();
        item.setId(rs.getInt("id"));
        item.setFeedbackId(rs.getInt("feedback_id"));

        String category = rs.getString("category");
        if (category != null) {
            item.setCategory(FeedbackCategory.valueOf(category));
        }

        item.setScore(rs.getInt("score"));
        item.setComment(rs.getString("comment"));
        return item;
    }
}
