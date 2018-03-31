package planets;

public class BlazingPlanet extends Planet {

    BlazingPlanet(String name) {
        super(name);
    }

    @Override
    public int getSpecialRocketTakeoffEnergy(){
        return 50;
    }

    @Override
    public String toString(){
        return String.format(ANSI_RED + "Blazing planet %s" + ANSI_RESET, getName());
    }

    @Override
    public boolean isBlazing() {
        return true;
    }

    @Override
    public boolean isSpecialService() {
        return false;
    }
}