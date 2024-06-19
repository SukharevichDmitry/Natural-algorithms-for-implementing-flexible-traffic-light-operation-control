package buffer;

import java.util.Arrays;
import java.util.Random;

public class TrafficBeeAlgorithm {
    private static final int NUM_BEES = 50;
    private static final int NUM_EMPLOYED_BEES = NUM_BEES / 2;
    private static final int NUM_ONLOOKER_BEES = NUM_BEES / 2;
    private static final int NUM_CYCLES = 100;
    private static final int MIN_GREEN_TIME = 10;
    private static final int MAX_GREEN_TIME = 60;
    private static final Random random = new Random();

    private static final int[] trafficData = {30, 20, 25, 35}; // Example traffic data for 4 sides of the intersection

    public static void main(String[] args) {
        int[] result = optimizeTraffic(trafficData);
        System.out.println("Optimized times: " + Arrays.toString(result));
    }

    private static int[] optimizeTraffic(int[] trafficData) {
        int[][] population = initializePopulation(NUM_BEES, trafficData.length);
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

    private static int[][] initializePopulation(int size, int numGenes) {
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
}
