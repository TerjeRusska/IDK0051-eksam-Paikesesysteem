package rocketlogistics;

import planets.Planet;
import postpackage.Package;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Logistics {
    List<Planet> getPlanetPriorityList(List<Package> packageList);

    List<Planet> getSolarSystemPlanets();

    Optional<Planet> getPlanetThatStillHasPackages(Predicate<Planet> condition);
}
