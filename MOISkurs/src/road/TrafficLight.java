package road;

import java.util.Arrays;
import java.util.Random;

public class TrafficLight {
    private static final boolean[] colourOfTLs = {false, false, false, false};
    private static final int[] queueOfTL = {0, 0, 0, 0};
    private static final int[] trafficData = new int[]{0,0,0,0};


    private static final int POPULATION_SIZE = 50;
    private static final int NUM_GENERATIONS = 100;
    private static final double MUTATION_RATE = 0.1;
    private static final int MIN_GREEN_TIME = 10;
    private static final int MAX_GREEN_TIME = 60;
    private static final Random random = new Random();


    private static final int NUM_BEES = 50;
    private static final int NUM_EMPLOYED_BEES = NUM_BEES / 2;
    private static final int NUM_ONLOOKER_BEES = NUM_BEES / 2;
    private static final int NUM_CYCLES = 100;


    public static void setColourOfTLs(int i) {
        Arrays.fill(colourOfTLs, false);
        colourOfTLs[i] = true;
    }


    public static void setColourOfTLs(int i, int j) {
        Arrays.fill(colourOfTLs, false);
        colourOfTLs[i] = true;
        colourOfTLs[j] = true;
    }

    public static void setQueueOfTL(int i) {

        for (int j = 0; j < queueOfTL.length; j++) {
            if (j < i) {
                queueOfTL[j] = 2;
            } else if (j == i) {
                queueOfTL[j] = 1;
            } else {
                queueOfTL[j] = 0;
            }
        }
    }

    public static boolean[] getColourOfTLs() {
        return colourOfTLs;
    }

    public static int[] getQueueOfTL() {
        return queueOfTL;
    }

    public static int[] getTrafficData() {
        return trafficData;
    }

    public static void addCar(Car car) {
        trafficData[car.getNumberOfRoad()]++;
    }

    public static void cleanTrafficData() {
            Arrays.fill(trafficData, 0);
    }


    public static int[] optimizeTraffic(int[] trafficData) {
        int[][] population = initializePopulation(POPULATION_SIZE, trafficData.length);

        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            population = evolvePopulation(population, trafficData);
        }

        return getBestSolution(population, trafficData);
    }

    private static int[][] initializePopulation(int size, int numGenes) {
        int[][] population = new int[size][numGenes];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < numGenes; j++) {
                population[i][j] = random.nextInt(MAX_GREEN_TIME - MIN_GREEN_TIME + 1) + MIN_GREEN_TIME;
            }
        }
        return population;
    }


    private static int[][] evolvePopulation(int[][] population, int[] trafficData) {
        int[][] newPopulation = new int[population.length][];
        for (int i = 0; i < population.length; i++) {
            int[] parent1 = selectParent(population, trafficData);
            int[] parent2 = selectParent(population, trafficData);

            int[] offspring = crossover(parent1, parent2);
            if (random.nextDouble() < MUTATION_RATE) {
                mutate(offspring);
            }

            newPopulation[i] = offspring;
        }
        return newPopulation;
    }

    private static int[] selectParent(int[][] population, int[] trafficData) {
        int tournamentSize = 5;
        int[] best = population[random.nextInt(population.length)];
        for (int i = 1; i < tournamentSize; i++) {
            int[] challenger = population[random.nextInt(population.length)];
            if (fitness(challenger, trafficData) < fitness(best, trafficData)) {
                best = challenger;
            }
        }
        return best;
    }

    private static int[] crossover(int[] parent1, int[] parent2) {
        int[] offspring = new int[parent1.length];
        for (int i = 0; i < parent1.length; i++) {
            offspring[i] = random.nextBoolean() ? parent1[i] : parent2[i];
            // Ensure offspring times are within bounds
            offspring[i] = Math.max(MIN_GREEN_TIME, Math.min(MAX_GREEN_TIME, offspring[i]));
        }
        return offspring;
    }

    private static void mutate(int[] individual) {
        int geneIndex = random.nextInt(individual.length);
        individual[geneIndex] = random.nextInt(MAX_GREEN_TIME - MIN_GREEN_TIME + 1) + MIN_GREEN_TIME;
    }

    private static int[] getBestSolution(int[][] population, int[] trafficData) {
        int[] best = population[0];
        for (int[] individual : population) {
            if (fitness(individual, trafficData) < fitness(best, trafficData)) {
                best = individual;
            }
        }
        return best;
    }

    private static int fitness(int[] solution, int[] trafficData) {
        int totalDelay = 0;
        for (int i = 0; i < solution.length; i++) {
            totalDelay += Math.abs(solution[i] - trafficData[i]);
        }
        return totalDelay;
    }





    public static int[] optimizeBeeTraffic(int[] trafficData) {
        int[][] population = initializeBeePopulation(NUM_BEES, trafficData.length);
        double[] fitness = calculateFitness(population, trafficData);

        for (int cycle = 0; cycle < NUM_CYCLES; cycle++) {
            // Employed bees phase
            for (int i = 0; i < NUM_EMPLOYED_BEES; i++) {
                int[] newSolution = generateNeighborSolution(population[i]);
                double newFitness = evaluateSolution(newSolution, trafficData);
                if (newFitness < fitness[i]) {
                    population[i] = newSolution;
                    fitness[i] = newFitness;
                }
            }

            // Calculate probabilities for onlooker bees
            double[] probabilities = calculateProbabilities(fitness);

            // Onlooker bees phase
            for (int i = 0; i < NUM_ONLOOKER_BEES; i++) {
                int selectedBee = selectBee(probabilities);
                int[] newSolution = generateNeighborSolution(population[selectedBee]);
                double newFitness = evaluateSolution(newSolution, trafficData);
                if (newFitness < fitness[selectedBee]) {
                    population[selectedBee] = newSolution;
                    fitness[selectedBee] = newFitness;
                }
            }

            // Scout bees phase
            for (int i = 0; i < NUM_BEES; i++) {
                if (shouldBecomeScout()) {
                    population[i] = generateRandomSolution(trafficData.length);
                    fitness[i] = evaluateSolution(population[i], trafficData);
                }
            }
        }

        return getBestSolution(population, fitness);
    }

    private static int[][] initializeBeePopulation(int size, int numGenes) {
        int[][] population = new int[size][numGenes];
        for (int i = 0; i < size; i++) {
            population[i] = generateRandomSolution(numGenes);
        }
        return population;
    }

    private static int[] generateRandomSolution(int numGenes) {
        int[] solution = new int[numGenes];
        for (int i = 0; i < numGenes; i++) {
            solution[i] = random.nextInt(MAX_GREEN_TIME - MIN_GREEN_TIME + 1) + MIN_GREEN_TIME;
        }
        return solution;
    }

    private static int[] generateNeighborSolution(int[] solution) {
        int[] newSolution = Arrays.copyOf(solution, solution.length);
        int index = random.nextInt(newSolution.length);
        newSolution[index] = random.nextInt(MAX_GREEN_TIME - MIN_GREEN_TIME + 1) + MIN_GREEN_TIME;
        return newSolution;
    }

    private static double[] calculateFitness(int[][] population, int[] trafficData) {
        double[] fitness = new double[population.length];
        for (int i = 0; i < population.length; i++) {
            fitness[i] = evaluateSolution(population[i], trafficData);
        }
        return fitness;
    }

    private static double evaluateSolution(int[] solution, int[] trafficData) {
        int totalDelay = 0;
        for (int i = 0; i < solution.length; i++) {
            totalDelay += Math.abs(solution[i] - trafficData[i]);
        }
        return totalDelay;
    }

    private static double[] calculateProbabilities(double[] fitness) {
        double[] probabilities = new double[fitness.length];
        double fitnessSum = Arrays.stream(fitness).sum();
        for (int i = 0; i < fitness.length; i++) {
            probabilities[i] = fitness[i] / fitnessSum;
        }
        return probabilities;
    }

    private static int selectBee(double[] probabilities) {
        double r = random.nextDouble();
        double cumulativeProbability = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulativeProbability += probabilities[i];
            if (r <= cumulativeProbability) {
                return i;
            }
        }
        return probabilities.length - 1; // Shouldn't get here
    }

    private static boolean shouldBecomeScout() {
        return random.nextDouble() < 0.1; // 10% chance to become a scout
    }

    private static int[] getBestSolution(int[][] population, double[] fitness) {
        int bestIndex = 0;
        for (int i = 1; i < population.length; i++) {
            if (fitness[i] < fitness[bestIndex]) {
                bestIndex = i;
            }
        }
        return population[bestIndex];
    }


    public static int[] optimizeHardTraffic(int[] trafficData){
        return new int[]{15, 15, 15, 15};
    }
}
