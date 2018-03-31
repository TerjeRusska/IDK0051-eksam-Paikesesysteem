package postpackage;

import planets.Planet;

public class Package {

    private Planet initialLocation;
    private Planet destination;
    private int weight;

    public Package(Planet initialLocation, Planet destination, int weight){
        this.initialLocation = initialLocation;
        this.destination = destination;
        this.weight = weight;
    }

    public Planet getInitialLocation() {
        return initialLocation;
    }

    public Planet getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public String toString(){
        return String.format("Package with a weight of: %d kg from %s to %s",
                this.weight, this.initialLocation, this.destination);
    }
}
