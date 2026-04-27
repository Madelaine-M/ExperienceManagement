package service.interfaces.suggestions;


import model.Incident;

public interface SuggestionResolver {
    boolean supports(Incident incident);
    SuggestionStrategy resolve(Incident incident);
}
