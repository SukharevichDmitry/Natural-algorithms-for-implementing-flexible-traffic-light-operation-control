package buffer;

import java.util.Arrays;
import java.util.Random;

public class TrafficGeneticAlgorithm {
    private static final int POPULATION_SIZE = 50;
    private static final int NUM_GENERATIONS = 100;
    private static final double MUTATION_RATE = 0.1;
    private static final int NUM_CROSSOVERS = 2;
    private static final Random random = new Random();

    public static void main(String[] args) {
        int[][] trafficData = {
                {30, 20, 25, 35}, // Example traffic data for 4 sides of the intersection
        };

        for (int[] data : trafficData) {
            int[] result = optimizeTraffic(data);
            System.out.println("Optimized times: " + Arrays.toString(result));
        }
    }

    private static int[] optimizeTraffic(int[] trafficData) {
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
                population[i][j] = random.nextInt(101); // Random initial times between 0 and 100 seconds
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
        }
        return offspring;
    }

    private static void mutate(int[] individual) {
        int geneIndex = random.nextInt(individual.length);
        individual[geneIndex] = random.nextInt(101); // New random time between 0 and 100 seconds
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
}
