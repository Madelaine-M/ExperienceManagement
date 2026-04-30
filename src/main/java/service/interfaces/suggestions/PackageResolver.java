package service.interfaces.suggestions;


import model.domain.Incident;
import model.enums.Packages;

public interface PackageResolver {
    Packages getPackage(Incident incident);
}
