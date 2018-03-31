package sensor;

public class CosmicRadiationSensor {

    private int resource = 25;

    public void useSensor(){
        resource --;
    }

    public void fixSensor(){
        resource = 25;
    }

    public int getResource() {
        return resource;
    }
}
