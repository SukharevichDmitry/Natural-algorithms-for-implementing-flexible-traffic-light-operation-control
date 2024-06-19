package road;

public class Car {
    private int speed;
    private final int way;
    private final int occupiedLine;
    private int positionOnRoad;
    private int numberOfRoad;
    private int timeForWait;
    private boolean isCrossroadReached;
    private boolean isCrossroadPassed;
    private final Crossroad crossroad;

    public Car(int speed,int numberOfRoad, int way, int occupiedLine, Crossroad crossroad){

        if(numberOfRoad == 2 || numberOfRoad == 3){
            this.positionOnRoad = crossroad.getRoadLenght();
        }
        else if(numberOfRoad == 0 || numberOfRoad == 1){
            this.positionOnRoad = 0;
        }

        this.speed = speed;
        this.way = way;
        this.occupiedLine = occupiedLine;
        this.numberOfRoad = numberOfRoad;
        this.crossroad = crossroad;
        timeForWait = 0;

        this.isCrossroadReached = false;
        this.isCrossroadPassed = false;

        crossroad.addCar(this);
        Visualisation.addCars(this);
        TrafficLight.addCar(this);
    }

    public void move(){
        if(!isCrossroadReached){
            moveBeforeCrossroad();
        }
        else if(!isCrossroadPassed){
            moveOnCrossroad();
        }
        else{
            moveAfterCrossroad();
        }
    }

    public void moveBeforeCrossroad(){

        if ((this.positionOnRoad <= crossroad.getRoadLenght()/2 - crossroad.getCountOfLines() *
                Visualisation.getStripeHeight() && this.numberOfRoad <=1)){

            this.positionOnRoad += speed*100/3600;
        }

        else if ((this.positionOnRoad >= crossroad.getRoadLenght()/2 + crossroad.getCountOfLines() *
                Visualisation.getStripeHeight() && this.numberOfRoad <=3 && this.numberOfRoad > 1)){

            this.positionOnRoad -= speed*100/3600;

        }
        else{
            this.isCrossroadReached = true;
        }


        //Замедление и остановка перед машиной, иначе перед светофором
        try {
            stopInFrontOfCar() ;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void stopInFrontOfCar()throws InterruptedException {
        if (isCarInFront()) {
            stop();
        }
        else{
            stopInFrontOfTrafficLight();
        }
    }

    private boolean isCarInFront() {
        for (Car car : crossroad.getCars()) {
            if (car != this
                    && this.numberOfRoad <=1
                    && car.occupiedLine == this.occupiedLine
                    && car.numberOfRoad == this.numberOfRoad
                    && car.positionOnRoad > this.positionOnRoad
                    && car.positionOnRoad - this.positionOnRoad < 40
            ) {
                return true;
            }

            else if (car != this
                    && this.numberOfRoad > 1
                    && this.numberOfRoad <=3
                    && car.occupiedLine == this.occupiedLine
                    && car.numberOfRoad == this.numberOfRoad
                    && car.positionOnRoad < this.positionOnRoad
                    && this.positionOnRoad - car.positionOnRoad < 40
                    && !this.isCrossroadReached) {
                return true;
            }
        }
        return false;
    }

    public void stopInFrontOfTrafficLight() {
        if(this.numberOfRoad <=1 && this.positionOnRoad >= crossroad.getRoadLenght()/2
                - crossroad.getCountOfLines() * Visualisation.getStripeHeight() - 40
                && !this.isCrossroadReached){

            if(!TrafficLight.getColourOfTLs()[this.numberOfRoad]){
                stop();
            }
            else{
                start();
            }
        }

        else if(this.numberOfRoad > 1 && this.numberOfRoad <=3
                && this.positionOnRoad <= crossroad.getRoadLenght()/2 + crossroad.getCountOfLines()
                * Visualisation.getStripeHeight() + 20
                && !this.isCrossroadReached){

            if(!TrafficLight.getColourOfTLs()[this.numberOfRoad]){
                stop();
            }
            else{
                start();
            }
        }
        else {
            start();
        }
    }


    private void stop(){
        if (this.speed > 0){
            if (this.speed - 5 < 0){
                this.speed = 0;
            }
            else {
                this.speed-= 5;
            }
        }
    }

    public void start(){
        if (this.speed + 3 > 60){
            this.speed = 60;
        }
        else {
            this.speed+= 3;
        }
    }



    public void moveOnCrossroad(){
        if(this.way == (this.numberOfRoad + 1) % 4){
            rotateToLeft();
        }
        else if(this.way == (this.numberOfRoad + 3) % 4){
            rotateToRight();
        }
        else if(this.way == (this.numberOfRoad + 2) % 4){
            moveForward();
        }
        else if(this.way == this.numberOfRoad){
            moveForward();
        }
    }


    public void rotateToLeft(){
        if(isRotationToLeftIsAllowed()){

            if(this.positionOnRoad >= crossroad.getRoadLenght()/2 && this.numberOfRoad <=1) {
                this.numberOfRoad = (this.numberOfRoad + 2) % 4;
                this.isCrossroadPassed = true;
            }

            else if((this.positionOnRoad < crossroad.getRoadLenght()/2 - 15) && this.numberOfRoad <=3
                    && this.numberOfRoad > 1){
                this.numberOfRoad = (this.way + 2) % 4;
                this.isCrossroadPassed = true;
            }

            else{
                if(this.numberOfRoad == 2 || this.numberOfRoad == 3){
                    this.positionOnRoad -= speed*100/3600;
                }
                else if(this.numberOfRoad == 0 || this.numberOfRoad == 1){
                    this.positionOnRoad += speed*100/3600;
                }
            }
        }
        else{
            moveForward();
        }
    }

    public boolean isRotationToLeftIsAllowed(){
        return this.occupiedLine == 0;
    }

    public void rotateToRight() {
        if (isRotationToRightIsAllowed()) {
                this.numberOfRoad = (this.way + 2) % 4;
                this.isCrossroadPassed = true;
        } else {
            moveForward();
        }
    }


    public boolean isRotationToRightIsAllowed(){
        return this.occupiedLine == crossroad.getCountOfLines() - 1;
    }

    public void moveForward(){
        if(positionOnRoad == crossroad.getRoadLenght()/2 + 30 * crossroad.getCountOfLines()){
            this.numberOfRoad = (this.numberOfRoad + 2) % crossroad.getCountOfLines();
            this.isCrossroadPassed = true;
        }
        else{
            if(this.numberOfRoad == 2 || this.numberOfRoad == 3){
                this.positionOnRoad -= speed*100/3600;
            }
            else if(this.numberOfRoad == 0 || this.numberOfRoad == 1){
                this.positionOnRoad += speed*100/3600;
            }
        }
    }


    public void moveAfterCrossroad(){

        if(this.numberOfRoad == 2 || this.numberOfRoad == 3){
            if (this.positionOnRoad != 0){
                this.positionOnRoad -= speed*100/3600;
            }
        }
        else if(this.numberOfRoad == 0 || this.numberOfRoad == 1){
            if (this.positionOnRoad != crossroad.getRoadLenght()){
                this.positionOnRoad += speed*100/3600;
            }
        }

    }

    public int getNumberOfRoad(){
        return this.numberOfRoad;
    }

    public int getOccupiedLine(){
        return this.occupiedLine;
    }

    public int getPositionOnRoad(){
        return this.positionOnRoad;
    }

    public boolean getIsCrossroadPassed(){
        return isCrossroadPassed;
    }

    public boolean getIsCrossroadReached(){
        return  isCrossroadReached;
    }

    public int getSpeed(){
        return this.speed;
    }

    public void addWait(){
        this.timeForWait++;
    }

    public int getTimeForWait(){
        return this.timeForWait;
    }
}


