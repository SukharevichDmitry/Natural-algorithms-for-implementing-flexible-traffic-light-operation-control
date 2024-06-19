package road;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;

public class Visualisation extends Application {
    private static int ROAD_LENGHT = 400;
    private static int COUNT_OF_STRIPES = 2;
    private static final int STRIPE_HEIGHT = 30;
    static ArrayList<Car> cars = new ArrayList<>();
    private final ArrayList<Rectangle> carsOnRoad = new ArrayList<>();
    static ArrayList<Car> carsOnCrossroad = new ArrayList<>();
    private Pane root;
    static Crossroad crossroad;
    static Circle[] trafficLightsColors = new Circle[12];
    int[] trafficLightTime = new int[4];
    int timer = trafficLightTime[0];

    int road0 = 78;
    int road1 = 43;
    int road2 = 81;
    int road3 = 62;

    int[] trafficData = {road0*2, road1*2, road2*2, road3*2};

    static ArrayList<ArrayList<Integer>> waitTime = new ArrayList<>(4);


    static int[] averageWaitTime = new int[4];


    public static void setWaitTime(){
        for (int i = 0; i < 4; i++) {
            waitTime.add(new ArrayList<>());
        }
    }
    public static int getStripeHeight(){
        return STRIPE_HEIGHT;
    }
    public static void setCrossroad(Crossroad _crossroad){
        ROAD_LENGHT = _crossroad.getRoadLenght();
        COUNT_OF_STRIPES = _crossroad.getCountOfLines();
        crossroad = _crossroad;
    }

    public static void addCars(Car car){
        cars.add(car);
        carsOnCrossroad.add(car);
    }

    public Rectangle createCar(Car car){
        int numberOfRoad = car.getNumberOfRoad();
        int occupiedLine = car.getOccupiedLine();
        Rectangle carOnRoad = null;

        if (numberOfRoad == 0){
            carOnRoad = new Rectangle(0, (double) ROAD_LENGHT /2 + 30*occupiedLine + 5, 20,20);
        } else if(numberOfRoad == 1){
            carOnRoad = new Rectangle((double) ROAD_LENGHT /2 - 30*(occupiedLine+1) + 5, 0, 20,20);
        } else if(numberOfRoad == 2){
            carOnRoad = new Rectangle(ROAD_LENGHT, (double) ROAD_LENGHT /2 - 30*(occupiedLine+1) + 5, 20,20);
        } else if(numberOfRoad == 3){
            carOnRoad = new Rectangle((double) ROAD_LENGHT /2 + 30*occupiedLine + 5, ROAD_LENGHT, 20,20);
        }
        if (carOnRoad != null) {
            carOnRoad.setFill(Color.GRAY);
        }
        return carOnRoad;
    }

    private void moveCars(ArrayList<Rectangle> carsOnRoad, ArrayList<Car> cars, Pane root) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.01), event -> {
            for (int i = 0; i < carsOnRoad.size(); i++) {
                double currentX = carsOnRoad.get(i).getX();
                double currentY = carsOnRoad.get(i).getY();
                int numberOfRoad = cars.get(i).getNumberOfRoad();

                cars.get(i).move();


                double newX = currentX;
                double newY = currentY;

                if (numberOfRoad == 0) {
                    newX = cars.get(i).getPositionOnRoad();
                    newY = (double) ROAD_LENGHT / 2 + 30 * cars.get(i).getOccupiedLine() + 5;
                } else if (numberOfRoad == 2) {
                    newX = cars.get(i).getPositionOnRoad();
                    newY = (double) ROAD_LENGHT / 2 - 30 * (cars.get(i).getOccupiedLine() + 1) + 5;
                } else if (numberOfRoad == 1) {
                    newX = (double) ROAD_LENGHT / 2 - 30 * (cars.get(i).getOccupiedLine() + 1) + 5;
                    newY = cars.get(i).getPositionOnRoad();
                } else if (numberOfRoad == 3) {
                    newX = (double) ROAD_LENGHT / 2 + 30 * cars.get(i).getOccupiedLine() + 5;
                    newY = cars.get(i).getPositionOnRoad();
                }

                carsOnRoad.get(i).setX(newX);
                carsOnRoad.get(i).setY(newY);
            }

            for (int i = 0; i < cars.size(); i++) {
                if (cars.get(i).getPositionOnRoad() == 0 || cars.get(i).getPositionOnRoad() == ROAD_LENGHT) {

                    crossroad.removeCar(cars.get(i));
                    Rectangle removedCar = carsOnRoad.remove(i);
                    root.getChildren().remove(removedCar);
                    cars.remove(i);
                    i--;
                }
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void delCars(){
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), event -> {
            for (int i=0; i<carsOnCrossroad.size(); i++){
                if(carsOnCrossroad.get(i).getIsCrossroadReached()){

                    trafficData[carsOnCrossroad.get(i).getNumberOfRoad()]--;

                    int roadNumber = carsOnCrossroad.get(i).getNumberOfRoad();
                    int timeForWait = carsOnCrossroad.get(i).getTimeForWait();

                    waitTime.get(roadNumber).add(timeForWait);

                    carsOnCrossroad.remove(carsOnCrossroad.get(i));
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void generateCars() {
        Timeline carGeneration = new Timeline(new KeyFrame(Duration.seconds(0.75), event -> {
            int randomThread = (int) (Math.random() * 4);

            for (int i=0; i<randomThread; i++){

                int numberOfRoad = (int) (Math.random() * 4);
                int way = (int) (Math.random() * 4);
                int occupiedLine = (int) (Math.random() * (COUNT_OF_STRIPES));

                Car car = new Car(60, numberOfRoad, way, occupiedLine, crossroad);

                Rectangle carOnRoad = createCar(car);
                carsOnRoad.add(carOnRoad);
                root.getChildren().add(carOnRoad);

            }
        }));

        carGeneration.setCycleCount(Animation.INDEFINITE);
        carGeneration.play();
    }

//    private void generateCars() {
//
//        Timeline carGeneration = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
//
//            if(road0!=0){
//                Car car0 = new Car(60, 0, 2, 0, crossroad);
//                Car car1 = new Car(60, 0, 2, 1, crossroad);
//                Rectangle carOnRoad0 = createCar(car0);
//                Rectangle carOnRoad1 = createCar(car1);
//                carsOnRoad.add(carOnRoad0);
//                carsOnRoad.add(carOnRoad1);
//
//                root.getChildren().add(carOnRoad0);
//                root.getChildren().add(carOnRoad1);
//                road0--;
//            }
//
//            if(road1!=0){
//                Car car0 = new Car(60, 1, 3, 0, crossroad);
//                Car car1 = new Car(60, 1, 3, 1, crossroad);
//                Rectangle carOnRoad0 = createCar(car0);
//                Rectangle carOnRoad1 = createCar(car1);
//                carsOnRoad.add(carOnRoad0);
//                carsOnRoad.add(carOnRoad1);
//
//                root.getChildren().add(carOnRoad0);
//                root.getChildren().add(carOnRoad1);
//                road1--;
//            }
//
//
//            if(road2!=0){
//                Car car0 = new Car(60, 2, 0, 0, crossroad);
//                Car car1 = new Car(60, 2, 0, 1, crossroad);
//                Rectangle carOnRoad0 = createCar(car0);
//                Rectangle carOnRoad1 = createCar(car1);
//                carsOnRoad.add(carOnRoad0);
//                carsOnRoad.add(carOnRoad1);
//
//                root.getChildren().add(carOnRoad0);
//                root.getChildren().add(carOnRoad1);
//                road2--;
//            }
//
//
//            if(road3!=0){
//                Car car0 = new Car(60, 3, 1, 0, crossroad);
//                Car car1 = new Car(60, 3, 1, 1, crossroad);
//                Rectangle carOnRoad0 = createCar(car0);
//                Rectangle carOnRoad1 = createCar(car1);
//                carsOnRoad.add(carOnRoad0);
//                carsOnRoad.add(carOnRoad1);
//
//                root.getChildren().add(carOnRoad0);
//                root.getChildren().add(carOnRoad1);
//                road3--;
//            }
//
//        }));
//
//        carGeneration.setCycleCount(Animation.INDEFINITE);
//        carGeneration.play();
//    }

    private void workTrafficLight(){

        Timeline workOfTrafficLight = new Timeline(new KeyFrame(Duration.seconds(1), event -> {

            for (int i=0; i <  TrafficLight.getQueueOfTL().length; i++){

                if(timer == 0){
                    timer = trafficLightTime[i];
                    TrafficLight.setQueueOfTL(i);

                    TrafficLight.cleanTrafficData();

                    for (Car car: crossroad.getCars()){
                        if(!car.getIsCrossroadPassed()){
                            TrafficLight.addCar(car);
                        }
                    }
                }

                else if(TrafficLight.getQueueOfTL()[i] == 1 && timer>0){

                    if(trafficData[(i+2)%4] != 0){
                        TrafficLight.setColourOfTLs(i, (i+2)%4);
                    }
                    else{
                        TrafficLight.setColourOfTLs(i);
                    }
                    timer--;
                }



                if(!TrafficLight.getColourOfTLs()[i]){
                    trafficLightsColors[i*3].setFill(Color.RED);
                    trafficLightsColors[i*3 + 2].setFill(Color.BLACK);
                }
                else {
                    trafficLightsColors[i*3].setFill(Color.BLACK);
                    trafficLightsColors[i*3 + 2].setFill(Color.GREEN);
                }
            }

        }));

        workOfTrafficLight.setCycleCount(Animation.INDEFINITE);
        workOfTrafficLight.play();
    }

//    private void setTLTime(){
//        Timeline setTLTime = new Timeline(new KeyFrame(Duration.seconds(30), event -> {
//
//            trafficLightTime = TrafficLight.optimizeBeeTraffic(TrafficLight.getTrafficData());
//            System.out.println("Traffic Light Time:\n " + Arrays.toString(trafficLightTime));
//        }));
//        setTLTime.setCycleCount(Animation.INDEFINITE);
//        setTLTime.play();
//    }

    private void setTLTime(){
            Timeline setTLTime = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
//
//                trafficLightTime = TrafficLight.optimizeBeeTraffic(trafficData);
//                trafficLightTime = TrafficLight.optimizeTraffic(trafficData);
                trafficLightTime = TrafficLight.optimizeHardTraffic(trafficData);
                System.out.println("Traffic Light Time:\n " + Arrays.toString(trafficLightTime));
                System.out.println("Traffic Data: \n" + Arrays.toString(trafficData));
            }));
        setTLTime.setCycleCount(Animation.INDEFINITE);
        setTLTime.play();
    }

    private void calculateWait(){
            Timeline calculateWait = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                for (int i=0; i<carsOnCrossroad.size(); i++){
                    if(carsOnCrossroad.get(i).getSpeed() == 0){
                        carsOnCrossroad.get(i).addWait();
                    }
                }
            }));
        calculateWait.setCycleCount(Animation.INDEFINITE);
        calculateWait.play();
    }

    private void calculateAverageWait(){
        Timeline calculateAverageWait = new Timeline(new KeyFrame(Duration.seconds(20), event -> {
            for (int i=0; i<waitTime.size(); i++){
                int sum = 0;
                for (int j=0; j<waitTime.get(i).size(); j++){
                    sum += waitTime.get(i).get(j);
                }
                averageWaitTime[i] = sum / (waitTime.get(i).size() + 1);
            }
            System.out.println("Average wait time: \n" + Arrays.toString(averageWaitTime));
        }));

        calculateAverageWait.setCycleCount(Animation.INDEFINITE);
        calculateAverageWait.play();
    }

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        root.setStyle("-fx-background-color: green;");

        int delta = 15;
        for (int stripe = 0; stripe < COUNT_OF_STRIPES; stripe++) {
            Rectangle stripe1Hor = new Rectangle(0, (double) ROAD_LENGHT / 2 - delta - 15,
                    ROAD_LENGHT, STRIPE_HEIGHT);
            Rectangle stripe2Hor = new Rectangle(0, (double) ROAD_LENGHT / 2 + delta - 15,
                    ROAD_LENGHT, STRIPE_HEIGHT);
            Rectangle markupHor1 = new Rectangle(0, (double) ROAD_LENGHT / 2,
                    (double) ROAD_LENGHT / 2 - 30 * COUNT_OF_STRIPES, 1);
            Rectangle markupHor2 = new Rectangle((double) ROAD_LENGHT / 2 + 30 * COUNT_OF_STRIPES,
                    (double) ROAD_LENGHT / 2, (double) ROAD_LENGHT / 2, 1);

            Rectangle stripe1Ver = new Rectangle((double) ROAD_LENGHT / 2 - delta - 15, 0,
                    STRIPE_HEIGHT, ROAD_LENGHT);
            Rectangle stripe2Ver = new Rectangle((double) ROAD_LENGHT / 2 + delta - 15, 0,
                    STRIPE_HEIGHT, ROAD_LENGHT);
            Rectangle markupVer1 = new Rectangle((double) ROAD_LENGHT / 2, 0, 1,
                    (double) ROAD_LENGHT / 2 - 30 * COUNT_OF_STRIPES);
            Rectangle markupVer2 = new Rectangle((double) ROAD_LENGHT / 2,
                    (double) ROAD_LENGHT / 2 + 30 * COUNT_OF_STRIPES,1,
                    (double) ROAD_LENGHT / 2);

            stripe1Hor.setFill(Color.BLACK);
            stripe2Hor.setFill(Color.BLACK);
            markupHor1.setFill(Color.WHITE);
            markupHor2.setFill(Color.WHITE);
            stripe1Ver.setFill(Color.BLACK);
            stripe2Ver.setFill(Color.BLACK);
            markupVer1.setFill(Color.WHITE);
            markupVer2.setFill(Color.WHITE);

            root.getChildren().addAll(stripe1Hor, stripe2Hor, markupHor1, markupHor2, stripe1Ver,
                    stripe2Ver, markupVer1, markupVer2);

            delta += 30;
        }

        // Traffic lights
        Rectangle[] trafficLights = new Rectangle[4];
        trafficLights[0] = new Rectangle((double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 30,
                (double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 10, 20, 60);
        trafficLights[1] = new Rectangle((double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 30,
                (double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 70, 20, 60);
        trafficLights[2] = new Rectangle((double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 10,
                (double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 70, 20, 60);
        trafficLights[3] = new Rectangle((double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 10,
                (double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 10, 20, 60);



        trafficLightsColors[0] = new Circle((double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 20,
                (double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 20, 8);
        trafficLightsColors[1] = new Circle((double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 20,
                (double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 40, 8);
        trafficLightsColors[2] = new Circle((double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 20,
                (double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 60, 8);

        trafficLightsColors[3] = new Circle((double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 20,
                (double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 60, 8);
        trafficLightsColors[4] = new Circle((double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 20,
                (double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 40, 8);
        trafficLightsColors[5] = new Circle((double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 20,
                (double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 20, 8);

        trafficLightsColors[6] = new Circle((double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 20,
                (double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 60, 8);
        trafficLightsColors[7] = new Circle((double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 20,
                (double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 40, 8);
        trafficLightsColors[8] = new Circle((double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 20,
                (double) ROAD_LENGHT / 2 - COUNT_OF_STRIPES * STRIPE_HEIGHT - 20, 8);

        trafficLightsColors[9] = new Circle((double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 20,
                (double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 20, 8);
        trafficLightsColors[10] = new Circle((double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 20,
                (double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 40, 8);
        trafficLightsColors[11] = new Circle((double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 20,
                (double) ROAD_LENGHT / 2 + COUNT_OF_STRIPES * STRIPE_HEIGHT + 60, 8);



        for (Rectangle trafficL : trafficLights) {
            trafficL.setFill(Color.DARKGRAY);
        }

        root.getChildren().addAll(trafficLights);

        for (Circle tlColor : trafficLightsColors) {
            tlColor.setFill(Color.BLACK);

        }
        root.getChildren().addAll(trafficLightsColors);


        // Add initial cars on the crossroad
        for (Car car : cars) {
            carsOnRoad.add(createCar(car));
        }
        root.getChildren().addAll(carsOnRoad);

        setWaitTime();

        // Move cars
        moveCars(carsOnRoad, cars, root);

        // Generate cars periodically
        generateCars();

        workTrafficLight();

        setTLTime();

        delCars();

        calculateWait();
        calculateAverageWait();

        Scene scene = new Scene(root, ROAD_LENGHT, ROAD_LENGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Bee");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}