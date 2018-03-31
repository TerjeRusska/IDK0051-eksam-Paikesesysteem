package postoffice;

import planets.Planet;
import postpackage.Package;
import rocketlogistics.Logistics;
import rockets.Rocket;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PostOffice implements Callable<Integer>{

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";

    private Planet planet;
    private boolean postOfficeOpen = true;
    private List<Package> processedPackages = new ArrayList<>();
    private List<Package> packagesToBeSentOut = new ArrayList<>();
    private List<Rocket> rocketWaitingList = new ArrayList<>();
    private final Object loc = new Object();
    private boolean postOfficeWorking = true;

    public PostOffice(Planet planet){
        this.planet = planet;
    }

    public void addNewPackage(Package postPackage){
        synchronized (loc){
            packagesToBeSentOut.add(postPackage);
            loc.notify();
        }
    }

    public Object getLoc(){
        return loc;
    }

    private void checkIfPlanetHasAnyRockets() throws InterruptedException {
        planet.checkRocketsOnPlanet();
    }

    public void addRocketToWaitingList(Rocket rocket){
        synchronized (rocketWaitingList){
            rocketWaitingList.add(rocket);
        }
    }

    public void removeRocketFromWaitingList(Rocket rocket){
        synchronized (rocketWaitingList){
            rocketWaitingList.remove(rocket);
            System.out.println(ANSI_YELLOW + rocket.getName() + " has left " + planet.getName() + " post office." + ANSI_RESET);
            rocketWaitingList.notifyAll();
        }
    }

    public List<Rocket> getRocketWaitingList(){
        synchronized (rocketWaitingList){
            return rocketWaitingList;
        }
    }

    public List<Package> unloadPackagesToProcess(List<Package> packages){
        synchronized (processedPackages) {
            List<Package> filteredPackages = packages.stream()
                    .filter(aPackage -> aPackage.getDestination().equals(planet))
                    .collect(Collectors.toList());

            filteredPackages.forEach(aPackage -> processedPackages.add(aPackage));
            return filteredPackages;
        }
    }

    public void rocketCheckup(Rocket rocket){
        if (rocket.getFuel() == 0 || !rocket.rocketCanTakeOff()){
            System.out.println(ANSI_GREEN + rocket.getName() + " has been refueled." + ANSI_RESET);
            rocketRefuel(rocket);
        } else if (new Random().nextBoolean() && rocket.getFuel() != 100){
            System.out.println(ANSI_GREEN + rocket.getName() + " has been refueled." + ANSI_RESET);
            rocketRefuel(rocket);
        }
    }

    public Optional<Package> getPackageFromPostOffice(Predicate<Package> condition){
        synchronized (packagesToBeSentOut) {
            Optional<Package> postPackage = packagesToBeSentOut.stream().filter(condition).findFirst();
            postPackage.ifPresent(aPackage -> packagesToBeSentOut.remove(aPackage));
            return postPackage;
        }
    }

    private void rocketRefuel(Rocket rocket){
        rocket.refuel();
    }

    public boolean hasPackagesToBeSentOut(){
        synchronized (packagesToBeSentOut){
            return !packagesToBeSentOut.isEmpty();
        }
    }

    public List<Package> getPackagesToBeSentOut(){
        return packagesToBeSentOut;
    }

    public int getPostOfficeStatsByCondition(Predicate<Package> condition){
        synchronized (processedPackages) {
            return (int) processedPackages.stream()
                    .filter(condition)
                    .count();
        }
    }

    public int getProcessedPackageAmount() {
        synchronized (processedPackages) {
            return processedPackages.size();
        }
    }

    public int getProcessedPackageTotalWeigth() {
        synchronized (processedPackages) {
            IntSummaryStatistics stats = processedPackages.stream()
                    .collect(Collectors.summarizingInt(Package::getWeight));
            return (int) stats.getSum();
        }
    }

    public double getProcessedPackageAverageWeigth() {
        synchronized (processedPackages) {
            IntSummaryStatistics stats = processedPackages.stream()
                    .collect(Collectors.summarizingInt(Package::getWeight));
            return (int) stats.getAverage();
        }
    }

    public List<Planet> processedPackageInitalLocations(){
        synchronized (processedPackages) {
            return processedPackages.stream()
                    .map(Package::getInitialLocation)
                    .distinct()
                    .collect(Collectors.toList());
        }
    }

    public boolean isPostOfficeOpen() {
        return postOfficeOpen;
    }

    void setPostOfficeStatus(boolean postOfficeOpen) {
        synchronized (loc){
            this.postOfficeOpen = postOfficeOpen;
            loc.notifyAll();
        }
    }

    @Override
    public Integer call() throws Exception {
        while (postOfficeWorking){
            checkIfPlanetHasAnyRockets();
        }
        System.out.println(ANSI_RED + planet.getName() + " post office has closed." + ANSI_RESET);
        return getProcessedPackageAmount();
    }

    void setPostOfficeWorking(boolean postOfficeWorking) {
        this.postOfficeWorking = postOfficeWorking;
    }
}
