import planets.Planet;
import postoffice.PackageStorage;
import postoffice.PostOfficeCentralCommand;
import rocketlogistics.Logistics;
import rocketlogistics.PackagePriorityLogistics;
import rockets.HeatResistantRocket;
import rockets.Rocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SolarSystem {

    public static void main(String[] args) {

        ExecutorService executor = Executors.newCachedThreadPool();

        PostOfficeCentralCommand centralCommand = new PostOfficeCentralCommand();

        Planet earth = Planet.PlanetCreator("Earth");
        Planet moon = Planet.PlanetCreator("Moon");
        Planet mars = Planet.PlanetCreator("Mars");
        Planet mercury = Planet.PlanetCreator("Mercury");
        Planet venus = Planet.PlanetCreator("Venus");
        Planet jupiter = Planet.PlanetCreator("Jupiter");
        Planet saturn = Planet.PlanetCreator("Saturn");
        Planet uranus = Planet.PlanetCreator("Uranus");
        Planet neptune = Planet.PlanetCreator("Neptune");
        Planet pluto = Planet.PlanetCreator("Pluto");
        Planet io = Planet.PlanetCreator("Io");

        List<Planet> solarSystemPlanets = Arrays.asList(earth, moon, mars,
                saturn, uranus, pluto, io, jupiter, neptune, mercury, venus);

        Logistics logistics = new PackagePriorityLogistics(solarSystemPlanets);

        for (Planet planet : solarSystemPlanets) {
            centralCommand.addPlanetToSystem(planet);
            //executor.submit(planet.getPostOffice());
        }

        Future<Integer> earthPostOfficeFuture = executor.submit(earth.getPostOffice());
        Future<Integer> moonPostOfficeFuture = executor.submit(moon.getPostOffice());
        Future<Integer> marsPostOfficeFuture = executor.submit(mars.getPostOffice());
        Future<Integer> mercuryPostOfficeFuture = executor.submit(mercury.getPostOffice());
        Future<Integer> venusPostOfficeFuture = executor.submit(venus.getPostOffice());
        Future<Integer> jupiterPostOfficeFuture = executor.submit(jupiter.getPostOffice());
        Future<Integer> saturnPostOfficeFuture = executor.submit(saturn.getPostOffice());
        Future<Integer> uranusPostOfficeFuture = executor.submit(uranus.getPostOffice());
        Future<Integer> neptunePostOfficeFuture = executor.submit(neptune.getPostOffice());
        Future<Integer> plutoPostOfficeFuture = executor.submit(pluto.getPostOffice());
        Future<Integer> ioPostOfficeFuture = executor.submit(io.getPostOffice());

        Runnable storage = new PackageStorage(solarSystemPlanets);
        executor.execute(storage);

        for (int i = 0; i < 20; i++) {
            Planet randomStartingPoint = solarSystemPlanets.get(new Random().nextInt(solarSystemPlanets.size()));
            while (randomStartingPoint.isBlazing()){
                randomStartingPoint = solarSystemPlanets.get(new Random().nextInt(solarSystemPlanets.size()));
            }
            centralCommand.addRocketToSystem(new Rocket("Rocket no. " + String.valueOf(i), randomStartingPoint, 100, logistics, centralCommand));
        }

        for (int i = 0; i < 5; i++) {
            Planet randomStartingPoint = solarSystemPlanets.get(new Random().nextInt(solarSystemPlanets.size()));
            centralCommand.addRocketToSystem(new HeatResistantRocket("Super Rocket no. " + String.valueOf(i), randomStartingPoint, 100, logistics, centralCommand));
        }

        centralCommand.getRocketList().forEach(executor::execute);

        executor.execute(centralCommand);

        try {
            System.out.println("Earth post office processed " + earthPostOfficeFuture.get() + " packages.");
            System.out.println("Jupiter post office processed " + jupiterPostOfficeFuture.get() + " packages.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
