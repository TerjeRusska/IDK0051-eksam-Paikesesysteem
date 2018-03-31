package testing;

import org.junit.Test;
import planets.Planet;
import postpackage.Package;
import rocketlogistics.Logistics;
import rocketlogistics.PackagePriorityLogistics;
import rockets.HeatResistantRocket;
import rockets.Rocket;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;


public class LogisticsTest {

    @Test
    public void testEmptyRegularRocketPackagePriorityLogistics() throws InterruptedException {
        Planet earth = Planet.PlanetCreator("Earth");
        Planet moon = Planet.PlanetCreator("Moon");
        Planet mars = Planet.PlanetCreator("Mars");
        Logistics logistics = new PackagePriorityLogistics(Arrays.asList(earth, moon, mars));

        earth.getPostOffice().addNewPackage(new Package(earth, moon, 25));
        earth.getPostOffice().addNewPackage(new Package(earth, earth, 25));
        earth.getPostOffice().addNewPackage(new Package(earth, mars, 25));
        earth.getPostOffice().addNewPackage(new Package(earth, moon, 25));
        earth.getPostOffice().addNewPackage(new Package(earth, mars, 25));
        earth.getPostOffice().addNewPackage(new Package(earth, moon, 25));
        earth.getPostOffice().addNewPackage(new Package(earth, moon, 25));

        Rocket rocket = new Rocket("Rocket", earth, 100, logistics, null);
        rocket.getPackagesFromPostOffice();

        assertTrue(rocket.getPackages().size() == 4);
    }

    @Test
    public void testRegularRocketPickPackagesByPriorityLogistics() throws InterruptedException {
        Planet earth = Planet.PlanetCreator("Earth");
        Planet moon = Planet.PlanetCreator("Moon");
        Planet mars = Planet.PlanetCreator("Mars");
        Logistics logistics = new PackagePriorityLogistics(Arrays.asList(earth, moon, mars));

        earth.getPostOffice().addNewPackage(new Package(earth, earth, 25));
        earth.getPostOffice().addNewPackage(new Package(earth, mars, 25));
        earth.getPostOffice().addNewPackage(new Package(earth, moon, 25));
        earth.getPostOffice().addNewPackage(new Package(earth, mars, 25));
        earth.getPostOffice().addNewPackage(new Package(earth, moon, 25));
        earth.getPostOffice().addNewPackage(new Package(earth, moon, 25));

        Rocket rocket = new Rocket("Rocket", earth, 100, logistics, null);
        rocket.addPackage(new Package(earth, moon, 25));
        rocket.getPackagesFromPostOffice();

        assertTrue(rocket.getPackages().stream()
                .filter(aPackage -> aPackage.getDestination() != moon)
                .collect(Collectors.toList()).isEmpty());
    }

    @Test
    public void testHeatResistantRocketPackageLogistics(){
        Planet jupiter = Planet.PlanetCreator("Jupiter");
        Planet neptune = Planet.PlanetCreator("Neptune");
        Planet moon = Planet.PlanetCreator("Moon");
        Planet mars = Planet.PlanetCreator("Mars");
        Logistics logistics = new PackagePriorityLogistics(Arrays.asList(jupiter, neptune, moon, mars));

        jupiter.getPostOffice().addNewPackage(new Package(jupiter, moon, 20));
        jupiter.getPostOffice().addNewPackage(new Package(jupiter, neptune, 20));
        jupiter.getPostOffice().addNewPackage(new Package(jupiter, jupiter, 20));
        jupiter.getPostOffice().addNewPackage(new Package(jupiter, neptune, 20));
        jupiter.getPostOffice().addNewPackage(new Package(jupiter, mars, 20));
        jupiter.getPostOffice().addNewPackage(new Package(jupiter, neptune, 20));

        Rocket superRocket = new HeatResistantRocket("Super rocket", jupiter, 100, logistics, null);

        assertTrue(superRocket.getPackages().stream()
                .filter(aPackage -> aPackage.getDestination() != moon && aPackage.getDestination() != mars)
                .collect(Collectors.toList()).isEmpty());
    }
}
