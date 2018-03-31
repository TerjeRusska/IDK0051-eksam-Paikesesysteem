package rockets;

import planets.Planet;
import postoffice.PostOffice;
import postoffice.PostOfficeCentralCommand;
import postpackage.Package;
import rocketlogistics.Logistics;
import sensor.CosmicRadiationSensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Rocket implements Runnable {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";

    private String name;
    private List<Package> packages = new ArrayList<>();
    private int fuel;
    private int totalFuelConsumtion = 0;
    private int currentWeigth = 0;
    private final int MAXIMUM_WEIGTH = 100;
    private final int TAKEOFF_ENERGY = 20;
    private int deliveredPackages = 0;
    private Planet location;
    private CosmicRadiationSensor sensor = new CosmicRadiationSensor();
    private Logistics logistics;
    private Planet manualNextPlanet;
    private boolean rocketWorking = true;
    private PostOfficeCentralCommand centralCommand;

    public Rocket(String name, Planet startingPoint, int fuel, Logistics logistics, PostOfficeCentralCommand centralCommand){
        this.name = name;
        this.location = startingPoint;
        this.fuel = fuel;
        this.logistics = logistics;
        this.centralCommand = centralCommand;
    }

    public void addPackage(Package postPackage){
        packages.add(postPackage);
        currentWeigth += postPackage.getWeight();
    }

    public void removePackage(Package postPackage){
        packages.remove(postPackage);
        currentWeigth -= postPackage.getWeight();
    }

    public boolean isRocketAbleToCarry(Package postPackage){
        return currentWeigth + postPackage.getWeight() <= MAXIMUM_WEIGTH && !postPackage.getDestination().isBlazing();
    }

    public void useFuel(int amount){
        this.fuel -= amount;
        this.totalFuelConsumtion += amount;
    }

    void setFuel(int amount){
        this.fuel = amount;
    }

    private Optional<Planet> findSpecialServicePlanet() {
        List<Planet> solasystemPlanets = logistics.getSolarSystemPlanets();

        Optional<Planet>  planet = packages.stream()
                .filter(aPackage -> aPackage.getDestination().isSpecialService()).findFirst()
                .map(Package::getDestination);
        if (!planet.isPresent()){
            planet = solasystemPlanets.stream()
                    .filter(Planet::isSpecialService)
                    .findFirst();
        }
        return planet;
    }

    private void setCourseToNewLocation() throws InterruptedException {

        if (packages.isEmpty() && manualNextPlanet == null){
            location = null;
        } else if (packages.isEmpty()){
            if (sensor.getResource() <= 2 && !manualNextPlanet.isSpecialService()){
                Optional<Planet> specialServicePlanet = findSpecialServicePlanet();
                if (!specialServicePlanet.isPresent()){
                    takeoff(manualNextPlanet);
                } else {
                    takeoff(specialServicePlanet.get());
                }
            } else {
                takeoff(manualNextPlanet);
            }
        } else {
            if (sensor.getResource() <= 2){
                Optional<Planet> specialServicePlanet = findSpecialServicePlanet();
                if (!specialServicePlanet.isPresent()){
                    takeoff(packages.get(0).getDestination());
                } else {
                    takeoff(specialServicePlanet.get());
                }
            } else {
                takeoff(packages.get(0).getDestination());
            }
        }
    }

    public void takeoff(Planet nextLocation) throws InterruptedException {
        if (rocketCanTakeOff() && sensor.getResource() > 0) {
            useFuel(TAKEOFF_ENERGY);
            sensor.useSensor();
            location.rocketLeavingPlanet(this);
            System.out.println(String.format(ANSI_CYAN +  "%s travelling to %s." + ANSI_RESET, name, nextLocation.getName()));
            Thread.sleep(200);
            setLocation(nextLocation);
            nextLocation.newRocketLanded(this);
        } else {
            System.out.println(String.format(ANSI_RED + "%s CAN'T TAKE OFF! Fuel: %d Sensor: %d" + ANSI_RESET, name, fuel, sensor.getResource()));
            location = null;
        }
    }

    public boolean rocketCanTakeOff(){
        return this.fuel - TAKEOFF_ENERGY >= 0;
    }

    public void getPackagesFromPostOffice() throws InterruptedException {
        synchronized (location.getPostOffice().getLoc()){
            List<Planet> planetPriorityList = logistics.getPlanetPriorityList(packages);
            List<Package> newPackages = new ArrayList<>();
            boolean anyPackagesGotten = false;

            if (!location.getPostOffice().isPostOfficeOpen()) {
                for (Planet planet : planetPriorityList) {
                    loadRocketWithSetOfPackages(
                            aPackage -> this.isRocketAbleToCarry(aPackage)  && aPackage.getDestination().equals(planet), newPackages);
                }
                loadRocketWithSetOfPackages(this::isRocketAbleToCarry, newPackages);
            } else {
                while (!anyPackagesGotten && location.getPostOffice().isPostOfficeOpen()) {
                    for (Planet planet : planetPriorityList) {
                        Optional<Package> postPackage = location.getPostOffice().getPackageFromPostOffice(
                                aPackage -> this.isRocketAbleToCarry(aPackage) && aPackage.getDestination().equals(planet));
                        while (postPackage.isPresent()){
                            addPackage(postPackage.get());
                            newPackages.add(postPackage.get());
                            anyPackagesGotten = true;
                            postPackage = location.getPostOffice().getPackageFromPostOffice(
                                    aPackage -> this.isRocketAbleToCarry(aPackage) && aPackage.getDestination().equals(planet));
                        }
                    }
                    Optional<Package> postPackage = location.getPostOffice().getPackageFromPostOffice(this::isRocketAbleToCarry);
                    while (postPackage.isPresent()){
                        addPackage(postPackage.get());
                        newPackages.add(postPackage.get());
                        anyPackagesGotten = true;
                        postPackage = location.getPostOffice().getPackageFromPostOffice(this::isRocketAbleToCarry);
                    }
                    if (!anyPackagesGotten && location.getPostOffice().isPostOfficeOpen()) {
                        location.getPostOffice().getLoc().wait();
                    }
                }
            }
            System.out.println(String.format("%s got new packages: %s", name, newPackages));
        }
    }

    private void loadRocketWithSetOfPackages(Predicate<Package> condition, List<Package> newPackages){
        Optional<Package> postPackage = location.getPostOffice().getPackageFromPostOffice(condition);

        while (postPackage.isPresent()){
            addPackage(postPackage.get());
            newPackages.add(postPackage.get());
            postPackage = location.getPostOffice().getPackageFromPostOffice(condition);
        }
    }

    void checkIfRoundTrip(){
        if (packages.isEmpty()){
            Optional<Planet> planet = logistics.getPlanetThatStillHasPackages(planet1 -> !planet1.isBlazing());
            if (planet.isPresent()){
                System.out.println(String.format(ANSI_CYAN + "%s setting manual location to %s" + ANSI_RESET, name, planet.get()));
                setManualNextPlanet(planet.get());
            } else {
                setManualNextPlanet(null);
            }
        }
    }

    void rocketWorkingLogic() throws InterruptedException {
        PostOffice postOffice = location.getPostOffice();
        synchronized (postOffice.getRocketWaitingList()){
            postOffice.addRocketToWaitingList(this);
            while (!postOffice.getRocketWaitingList().get(0).equals(this)){
                System.out.println(String.format(ANSI_YELLOW + "%s waiting in line with %d others." + ANSI_RESET, name, postOffice.getRocketWaitingList().size() - 1));
                postOffice.getRocketWaitingList().wait();
            }
        }
        List<Package> offTheRocket = postOffice.unloadPackagesToProcess(packages);
        deliveredPackages += offTheRocket.size();
        offTheRocket.forEach(this::removePackage);
        System.out.println(String.format("%s unloaded packages: %s", name, offTheRocket));
        postOffice.rocketCheckup(this);
        getPackagesFromPostOffice();
        postOffice.removeRocketFromWaitingList(this);
        checkIfRoundTrip();
        setCourseToNewLocation();
    }

    @Override
    public void run() {
        setLocation(location);
        location.newRocketLanded(this);
        while (!Thread.interrupted() && location != null){
            try {
                rocketWorkingLogic();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setRocketWorking(false);
        centralCommand.rocketFinishedJob(this);
        System.out.println(String.format("%s finished working, delivered %d packages with a total fuel consumption of %d",
                name , deliveredPackages, totalFuelConsumtion));
    }

    public int getFuel() {
        return fuel;
    }

    public void refuel(){
        this.fuel = 100;
    }

    public int getCurrentWeigth(){
        return currentWeigth;
    }

    Planet getLocation() {
        return location;
    }

    Logistics getLogistics(){
        return logistics;
    }

    public void setLocation(Planet location) {
        this.location = location;
    }

    public CosmicRadiationSensor getSensor() {
        return sensor;
    }

    public int getTotalFuelConsumtion() {
        return totalFuelConsumtion;
    }

    public void addToTotalFuelConsumtion(int amount){
        totalFuelConsumtion += amount;
    }

    public List<Package> getPackages() {
        return packages;
    }

    void setManualNextPlanet(Planet manualNextPlanet) {
        this.manualNextPlanet = manualNextPlanet;
    }

    public String getName() {
        return name;
    }

    public boolean isRocketWorking() {
        return rocketWorking;
    }

    public void setRocketWorking(boolean rocketWorking) {
        this.rocketWorking = rocketWorking;
    }
}
