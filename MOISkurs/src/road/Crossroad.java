package road;
import java.util.ArrayList;

public class Crossroad {
    private final int countOfLines;
    private final int roadLenght;
    private final ArrayList<Car> cars = new ArrayList<>();

    public Crossroad(int countOfLines, int roadLenght){
        this.countOfLines = countOfLines;
        this.roadLenght = roadLenght;
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void removeCar(Car car){
        cars.remove(car);
    }

    public ArrayList<Car> getCars() {
        return cars;
    }

    public int getRoadLenght() {
        return roadLenght;
    }

    public int getCountOfLines(){
        return countOfLines;
    }
}
