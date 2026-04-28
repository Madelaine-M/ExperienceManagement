package service.interfaces.suggestions;


import model.Incident;
import model.enums.Packages;

public interface PackageResolver {
    Packages getPackage(Incident incident);
}
