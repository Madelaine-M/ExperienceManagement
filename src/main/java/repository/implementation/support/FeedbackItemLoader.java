package repository.implementation.support;

import model.FeedbackItem;
import repository.implementation.mapper.FeedbackItemResultSetMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackItemLoader {
    private final FeedbackItemResultSetMapper feedbackItemMapper;

    public FeedbackItemLoader() {
        this(new FeedbackItemResultSetMapper());
    }

    public FeedbackItemLoader(FeedbackItemResultSetMapper feedbackItemMapper) {
        this.feedbackItemMapper = feedbackItemMapper;
    }

    public List<FeedbackItem> loadItemsByFeedbackId(Connection conn, int feedbackId) throws SQLException {
        List<FeedbackItem> items = new ArrayList<>();
        String sql = "SELECT * FROM feedback_items WHERE feedback_id = ? ORDER BY id;";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, feedbackId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(feedbackItemMapper.map(rs));
                }
            }
        }

        return items;
    }
}
