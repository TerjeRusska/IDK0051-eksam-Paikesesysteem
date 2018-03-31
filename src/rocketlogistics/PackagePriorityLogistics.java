package rocketlogistics;

import planets.Planet;
import postpackage.Package;

import java.awt.print.PrinterAbortException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PackagePriorityLogistics implements Logistics {

    private List<Planet> planets = new ArrayList<>();

    public PackagePriorityLogistics(List<Planet> planets){
        this.planets = planets;
    }

    @Override
    public List<Planet> getPlanetPriorityList(List<Package> packageList) {
        Map<Planet, Integer> priorityMap = new HashMap<>();
        for (Package pack : packageList){
            if (priorityMap.containsKey(pack.getDestination())){
                priorityMap.put(pack.getDestination(), priorityMap.get(pack.getDestination()) + 1);
            } else {
                priorityMap.put(pack.getDestination(), 1);
            }
        }
        return priorityMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<Planet> getSolarSystemPlanets() {
        return planets;
    }

    @Override
    public Optional<Planet> getPlanetThatStillHasPackages(Predicate<Planet> condition) {
        List<Planet> planetsWithPackages = planets.stream()
                .filter(planet -> planet.getPostOffice().hasPackagesToBeSentOut())
                .filter(condition)
                .collect(Collectors.toList());

        Optional<Planet> planet = Optional.empty();
        if (!planetsWithPackages.isEmpty()) {
            planet = Optional.of(planetsWithPackages.get(new Random().nextInt(planetsWithPackages.size())));
        }
        return planet;
    }
}
