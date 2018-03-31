package postoffice;


import planets.Planet;
import postpackage.Package;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PackageStorage implements Runnable{

    private Random random = new Random();
    private List<Planet> planets = new ArrayList<>();

    public PackageStorage(List<Planet> planets){
        this.planets = planets;
    }

    private void distributePackagesToPostOffices() throws InterruptedException {
        for (int i = 0; i < 1500; i++) {
            Planet randomInitialLocation = planets.get(random.nextInt(planets.size()));
            Planet randomDestinationLocation = planets.get(random.nextInt(planets.size()));
            int randomWeight = random.nextInt(79) + 1;
            Package postPackage = new Package(randomInitialLocation, randomDestinationLocation, randomWeight);
            randomInitialLocation.getPostOffice().addNewPackage(postPackage);
            Thread.sleep(3);
        }
    }

    private void closePostOffices(){
        for (Planet planet : planets){
            planet.getPostOffice().setPostOfficeStatus(false);
        }
    }

    @Override
    public void run() {
        try {
            distributePackagesToPostOffices();
            System.out.println("All packages have been distributed");
            closePostOffices();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
