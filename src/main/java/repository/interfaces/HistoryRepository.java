package repository.interfaces;

import model.SentimentHistory;

import java.util.List;

public interface HistoryRepository {

    void save(SentimentHistory historyEntry);

    SentimentHistory findById(int id);

    List<SentimentHistory> findByCustomerId(int customerId);

    void deleteById(int id);
}
