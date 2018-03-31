package postoffice;

import planets.Planet;
import rockets.Rocket;

public class ServicePostOffice extends PostOffice{

    public ServicePostOffice(Planet planet) {
        super(planet);
    }

    @Override
    public void rocketCheckup(Rocket rocket){

        if (rocket.getSensor().getResource() <= 2){
            System.out.println(ANSI_BLUE + rocket.getName() + " sensor has been fixed." + ANSI_RESET);
            rocket.getSensor().fixSensor();
        }
        super.rocketCheckup(rocket);
    }
}
