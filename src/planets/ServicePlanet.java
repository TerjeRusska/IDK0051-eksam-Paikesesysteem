package planets;

import postoffice.ServicePostOffice;

public class ServicePlanet extends Planet {

    private ServicePostOffice postOffice = new ServicePostOffice(this);

    ServicePlanet(String name) {
        super(name);
    }

    @Override
    public String toString(){
        return String.format(ANSI_BLUE + "Special service planet %s" + ANSI_RESET, getName());
    }

    @Override
    public boolean isBlazing() {
        return false;
    }

    @Override
    public boolean isSpecialService() {
        return true;
    }

    @Override
    public ServicePostOffice getPostOffice() {
        return postOffice;
    }
}
