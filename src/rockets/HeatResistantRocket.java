package rockets;

import planets.Planet;
import postoffice.PostOfficeCentralCommand;
import postpackage.Package;
import rocketlogistics.Logistics;

import java.util.List;
import java.util.Optional;

public class HeatResistantRocket extends Rocket {

    private final int MAXIMUM_WEIGTH = 80;

    public HeatResistantRocket(String name, Planet startingPoint, int fuel, Logistics logistics, PostOfficeCentralCommand centralCommand) {
        super(name, startingPoint, fuel, logistics, centralCommand);
    }

    @Override
    public void takeoff(Planet nextLocation) throws InterruptedException {
        if (rocketCanTakeOff() && getSensor().getResource() > 0) {
            useFuel(getLocation().getSpecialRocketTakeoffEnergy());
            getSensor().useSensor();
            getLocation().rocketLeavingPlanet(this);
            System.out.println(String.format(ANSI_CYAN +  "%s travelling to %s." + ANSI_RESET, getName(), nextLocation.getName()));
            Thread.sleep(500);
            setLocation(nextLocation);
            nextLocation.newRocketLanded(this);
        } else {
            System.out.println(String.format(ANSI_RED + "%s CAN'T TAKE OFF! Fuel: %d Sensor: %d" + ANSI_RESET, getName(), getFuel(), getSensor().getResource()));
            setLocation(null);
        }
    }

    @Override
    void checkIfRoundTrip(){
        if (getPackages().isEmpty()){
            Optional<Planet> planet = getLogistics().getPlanetThatStillHasPackages(planet1 -> planet1.getPostOffice().hasPackagesToBeSentOut());
            if (planet.isPresent()){
                System.out.println(String.format(ANSI_CYAN + "%s setting manual location to %s" + ANSI_RESET, getName(), planet.get()));
                setManualNextPlanet(planet.get());
            } else {
                setManualNextPlanet(null);
            }
        }
    }

    @Override
    public boolean isRocketAbleToCarry(Package postPackage){
        return getCurrentWeigth() + postPackage.getWeight() <= MAXIMUM_WEIGTH &&
                (postPackage.getDestination().isBlazing() || postPackage.getInitialLocation().isBlazing());
    }

    @Override
    public boolean rocketCanTakeOff(){
        return getFuel() - getLocation().getSpecialRocketTakeoffEnergy() >= 0;
    }
}
