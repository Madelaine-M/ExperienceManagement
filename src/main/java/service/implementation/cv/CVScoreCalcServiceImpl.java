package service.implementation.cv;

import model.domain.CVCustomer;
import service.interfaces.internal.CVScoreCalcService;
import service.interfaces.internal.CVScoringRuleService;

import java.util.List;

public class CVScoreCalcServiceImpl implements CVScoreCalcService {
    private final List<CVScoringRuleService> rules;

    public CVScoreCalcServiceImpl(List<CVScoringRuleService> rules) {
        this.rules = rules;
    }

    @Override
    public int calculate(CVCustomer customer) {
        int score = 0;
        for (CVScoringRuleService rule : rules) {
            score += rule.evaluate(customer);
        }
        return score;
    }
}