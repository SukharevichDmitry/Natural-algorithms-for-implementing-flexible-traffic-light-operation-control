package management;

import javafx.application.Application;
import road.Crossroad;
import road.Visualisation;
import road.Visualisation1;

public class Main {

    public static void main(String[] args) {
        Crossroad crossroad = new Crossroad(2, 1000);
        Visualisation.setCrossroad(crossroad);
        Visualisation1.setCrossroad(crossroad);

        Application.launch(Visualisation.class, args);
    }
}