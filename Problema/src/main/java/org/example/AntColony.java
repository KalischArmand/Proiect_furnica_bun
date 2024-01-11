package org.example;
import java.util.Arrays;
import java.util.Random;

public class AntColony {
    Ant[] ants;
    int[][] foodMatrix;
    int[][] distanceMatrix;
    Random random;
    int bestFitness;
    int bestFoodCollected;
    int[] bestPath;
    int generation;

    public AntColony(int numAnts, int matrixSize) {
        ants = new Ant[numAnts];
        foodMatrix = new int[matrixSize][matrixSize];
        distanceMatrix = initializeDistanceMatrix(matrixSize, matrixSize);
        random = new Random(42);
        bestFitness = 0;
        bestFoodCollected = 0;
        bestPath = new int[0];
        generation = 0;

        for (int i = 0; i < numAnts; i++) {
            int startX = random.nextInt(matrixSize);
            int startY = random.nextInt(matrixSize);
            ants[i] = new Ant(0);
            ants[i].generateRandomPath(matrixSize, 0.1, generation, startX, startY);
            ants[i].calculateFitness(foodMatrix, distanceMatrix);
        }

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                foodMatrix[i][j] = random.nextInt(2);
            }
        }

        System.out.println("Matricea hranei inițiale:");
        for (int[] row : foodMatrix) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
    }

    void crossover(Ant parent1, Ant parent2, Ant child) {
        int crossoverPoint = random.nextInt(Math.min(parent1.path.length, parent2.path.length));
        child.path = Arrays.copyOf(parent1.path, crossoverPoint);

        // Concatenează restul traseului de la părintele 2
        int remainingLength = parent2.path.length - crossoverPoint;
        int[] remainingPath = Arrays.copyOfRange(parent2.path, crossoverPoint, crossoverPoint + remainingLength);
        child.path = Arrays.copyOf(child.path, crossoverPoint + remainingLength);
        System.arraycopy(remainingPath, 0, child.path, crossoverPoint, remainingLength);
    }

    void mutate(Ant ant, double mutationRate) {
        if (random.nextDouble() < mutationRate) {
            int mutationSize = random.nextInt(Math.max(1, ant.path.length / 2)) + 1;
            int startIndex = random.nextInt(Math.max(1, ant.path.length - mutationSize + 1));

            for (int i = startIndex; i < startIndex + mutationSize && i < ant.path.length; i++) {
                ant.path[i] = random.nextInt(4);
            }
        }
    }

    void localSearchEvolve(int generations, double mutationRate, int optimalPathPrintInterval, int localSearchInterval, double penaltyFactor) {
        for (generation = 1; generation <= generations; generation++) {
            penaltyFactor *= 0.95;

            for (Ant ant : ants) {
                ant.generateRandomPath(foodMatrix.length, penaltyFactor, generation, random.nextInt(foodMatrix.length), random.nextInt(foodMatrix[0].length));
                ant.calculateFitness(foodMatrix, distanceMatrix);
            }

            if (generation % localSearchInterval == 0) {
                for (Ant ant : ants) {
                    ant.localSearch(foodMatrix, distanceMatrix);
                }
            }

            Ant bestAnt = ants[0];
            for (Ant ant : ants) {
                if (ant.fitness > bestAnt.fitness) {
                    bestAnt = ant;
                }
            }

            if (bestAnt.fitness > bestFitness) {
                bestFitness = bestAnt.fitness;
                bestFoodCollected = bestAnt.foodCollected;
                bestPath = Arrays.copyOf(bestAnt.path, bestAnt.path.length);
            }

            if (generation % optimalPathPrintInterval == 0) {
                System.out.println("Generația " + generation + ":");
                System.out.println("Traseul cel mai optim al generației: Furnica " + Arrays.toString(bestAnt.path) +
                        ", Fitness - " + bestAnt.fitness + ", Hrană colectată - " + bestAnt.foodCollected);
            }

            Ant[] newAnts = new Ant[ants.length];
            for (int i = 0; i < ants.length; i++) {
                Ant parent1 = selectParent();
                Ant parent2 = selectParent();
                newAnts[i] = new Ant(0);
                crossover(parent1, parent2, newAnts[i]);
                mutate(newAnts[i], mutationRate);
            }

            newAnts[random.nextInt(ants.length)] = bestAnt;
            ants = newAnts;
        }

        System.out.println("\nCel mai bun fitness final: " + bestFitness);
        System.out.println("Cel mai bun traseu final: Furnica " + Arrays.toString(bestPath));
        System.out.println("Numărul de hrană colectată: " + bestFoodCollected);
    }

    Ant selectParent() {
        double totalFitness = 0;
        for (Ant ant : ants) {
            totalFitness += ant.fitness;
        }

        double rand = random.nextDouble() * totalFitness;
        double cumulativeFitness = 0;

        for (Ant ant : ants) {
            cumulativeFitness += ant.fitness;
            if (cumulativeFitness >= rand) {
                return ant;
            }
        }

        return ants[random.nextInt(ants.length)];
    }

    private int[][] initializeDistanceMatrix(int rows, int cols) {
        int[][] distanceMatrix = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                distanceMatrix[i][j] = 1;
            }
        }
        return distanceMatrix;
    }
}
