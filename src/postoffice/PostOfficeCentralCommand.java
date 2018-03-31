package postoffice;

import planets.Planet;
import postpackage.Package;
import rockets.Rocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PostOfficeCentralCommand implements Runnable{

    private List<Rocket> rocketList = new ArrayList<>();
    private List<Planet> planets = new ArrayList<>();
    private boolean commandState = true;

    public void addRocketToSystem(Rocket rocket){
        rocketList.add(rocket);
    }

    public List<Rocket> getRocketList(){
        return rocketList;
    }

    public void addPlanetToSystem(Planet planet){
        planets.add(planet);
    }

    public void rocketFinishedJob(Rocket rocket){
        synchronized (rocketList){
            if (rocketList.contains(rocket)){
                rocketList.notify();
            } else {
                System.out.println("Unknown rocket");
            }
        }
    }

    private void closeAllPostOffices(){
        planets.stream().map(planet -> planet.getPostOffice())
                .forEach(postOffice -> postOffice.setPostOfficeWorking(false));

        planets.forEach(planet -> {
            try {
                planet.postOfficeShuttingDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        commandState = false;
    }

    private boolean checkIfAllRocketsHaveFinished() throws InterruptedException {
        synchronized (rocketList){
            List<Rocket> workingRockets = rocketList.stream().filter(Rocket::isRocketWorking).collect(Collectors.toList());
            if (!workingRockets.isEmpty()){
                if (new Random().nextBoolean()){
                    Planet randomPlanet = planets.get(new Random().nextInt(planets.size()));
                    Planet randomPlanet2 = planets.get(new Random().nextInt(planets.size()));
                    System.out.println("Central command is asking " + randomPlanet.getName() + " about " + randomPlanet2.getName());
                    requestPostOfficeStats(randomPlanet, aPackage -> aPackage.getInitialLocation() == randomPlanet2);
                }
                rocketList.wait();
            }
            return workingRockets.isEmpty();
        }
    }

    private void requestPostOfficeStats(Planet planet, Predicate<Package> condition){
        System.out.println(planet.getName() + " Post office requested stats: " + planet.getPostOffice().getPostOfficeStatsByCondition(condition));
    }

    @Override
    public void run() {
        while (commandState){
            try {
                if (checkIfAllRocketsHaveFinished()){
                    closeAllPostOffices();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Central command notified all post offices to stop working.");

    }
}
