package service.interfaces.suggestions;


import model.domain.Incident;

public interface SuggestionResolver {
    boolean supports(Incident incident);
    SuggestionStrategy resolve(Incident incident);
}
