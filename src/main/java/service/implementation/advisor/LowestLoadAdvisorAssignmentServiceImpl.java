package service.implementation.advisor;

import model.domain.Advisor;
import repository.interfaces.AdvisorRepository;
import repository.interfaces.CustomerSearch;
import service.interfaces.internal.AdvisorAssignmentService;

import java.util.Comparator;
import java.util.List;

public class LowestLoadAdvisorAssignmentServiceImpl implements AdvisorAssignmentService {
    private final AdvisorRepository advisorRepository;
    private final CustomerSearch customerSearch;

    public LowestLoadAdvisorAssignmentServiceImpl(AdvisorRepository advisorRepository,
                                                  CustomerSearch customerSearch) {
        this.advisorRepository = advisorRepository;
        this.customerSearch = customerSearch;
    }

    @Override
    public int assignAdvisorIdForNewCustomer() {
        List<Advisor> advisors = advisorRepository.findAll();
        if (advisors == null || advisors.isEmpty()) {
            throw new IllegalStateException("No advisors available for customer assignment.");
        }

        //AI suggested method structure
        return advisors.stream()
                .min(Comparator
                        .comparingInt(this::assignedCustomerCount)
                        .thenComparingInt(Advisor::getId))
                .map(Advisor::getId)
                .orElseThrow(() -> new IllegalStateException("Could not resolve advisor assignment."));
    }

    private int assignedCustomerCount(Advisor advisor) {
        return customerSearch.findByAdvisorId(advisor.getId()).size();
    }
}
