package planets;

import postoffice.PostOffice;
import rockets.Rocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Planet {
    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_BLUE = "\u001B[34m";
    static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";


    private String name;
    private PostOffice postOffice = new PostOffice(this);
    private List<Rocket> rocketsOnPlanet = new ArrayList<>();

    public Planet(String name){
        this.name = name;
    }

    public static Planet PlanetCreator(String name){
        switch (name) {
            case "Mercury":
            case "Venus":
                return new BlazingPlanet(name);
            case "Jupiter":
            case "Neptune":
                return new ServicePlanet(name);
            default:
                return new Planet(name);
        }
    }

    public void newRocketLanded(Rocket rocket){
        synchronized (rocketsOnPlanet){
            rocketsOnPlanet.add(rocket);
            rocketsOnPlanet.notify();
            System.out.println(ANSI_CYAN + rocket.getName() + " has laded on " + name + ANSI_RESET);
        }
    }

    public void checkRocketsOnPlanet() throws InterruptedException {
        synchronized (rocketsOnPlanet){
            if (rocketsOnPlanet.isEmpty()){
                System.out.println(ANSI_GREEN + name + " postoffice is waiting." + ANSI_RESET);
                rocketsOnPlanet.wait();
            }
        }
    }

    public void postOfficeShuttingDown() throws InterruptedException {
        synchronized (rocketsOnPlanet) {
            rocketsOnPlanet.notify();
        }
    }

    public void rocketLeavingPlanet(Rocket rocket){
        synchronized (rocketsOnPlanet){
            rocketsOnPlanet.notify();
            rocketsOnPlanet.remove(rocket);
        }
    }

    public int getSpecialRocketTakeoffEnergy() {
        return 25;
    }

    public String getName(){
        return this.name;
    }

    public String toString(){
        return String.format(ANSI_PURPLE + "Regular planet %s" + ANSI_RESET, this.name);
    }

    public boolean isBlazing() {
        return false;
    }

    public boolean isSpecialService() {
        return false;
    }

    public PostOffice getPostOffice() {
        return postOffice;
    }
}
