package org.example;

import java.util.Arrays;
import java.util.Random;

public class Ant {
    int[] path;
    int fitness;
    int foodCollected;

    public Ant(int pathLength) {
        path = new int[pathLength];
        fitness = 0;
        foodCollected = 0;
    }

    void generateRandomPath(int matrixSize, double penaltyFactor, int generation, int startX, int startY) {
        Random random = new Random();
        int[] directions = new int[]{0, 1, 2, 3};
        int prevDirection = -1;

        int pathLength = (int) Math.ceil((matrixSize * matrixSize) * (1.0 - Math.exp(-generation / 100.0)));
        path = new int[pathLength];

        int x = startX;
        int y = startY;

        for (int i = 0; i < pathLength; i++) {
            int direction;
            int[] allowedDirections;

            if (prevDirection == -1) {
                allowedDirections = directions;
            } else {
                allowedDirections = getAllowedDirections(prevDirection);
            }

            direction = allowedDirections[random.nextInt(allowedDirections.length)];
            path[i] = direction;
            prevDirection = direction;

            // Actualizează poziția furnicii
            switch (direction) {
                case 0: // North
                    y = (y - 1 + matrixSize) % matrixSize;
                    break;
                case 1: // South
                    y = (y + 1) % matrixSize;
                    break;
                case 2: // East
                    x = (x + 1) % matrixSize;
                    break;
                case 3: // West
                    x = (x - 1 + matrixSize) % matrixSize;
                    break;
            }
        }

        fitness -= penaltyFactor * (pathLength - 1);
    }

    void localSearch(int[][] foodMatrix, int[][] distanceMatrix) {
        int[] originalPath = Arrays.copyOf(path, path.length);
        int originalFitness = fitness;
        int originalFoodCollected = foodCollected;

        for (int i = 0; i < path.length; i++) {
            int originalDirection = path[i];
            int[] directions = new int[]{0, 1, 2, 3};

            for (int newDirection = 0; newDirection < directions.length; newDirection++) {
                if (newDirection != originalDirection) {
                    path[i] = directions[newDirection];
                    calculateFitness(foodMatrix, distanceMatrix);

                    if (fitness > originalFitness) {
                        originalFitness = fitness;
                        originalFoodCollected = foodCollected;
                    }
                }
            }

            path[i] = originalDirection;
        }

        System.arraycopy(originalPath, 0, path, 0, path.length);
        fitness = originalFitness;
        foodCollected = originalFoodCollected;
    }

    void calculateFitness(int[][] foodMatrix, int[][] distanceMatrix) {
        int x = 0, y = 0;
        int distance = 0;
        foodCollected = 0;

        boolean[][] visitedFood = new boolean[foodMatrix.length][foodMatrix[0].length];

        for (int i = 0; i < path.length; i++) {
            int direction = path[i];

            int newX = x, newY = y;
            switch (direction) {
                case 0: // North
                    newY = (y - 1 + foodMatrix.length) % foodMatrix.length;
                    break;
                case 1: // South
                    newY = (y + 1) % foodMatrix.length;
                    break;
                case 2: // East
                    newX = (x + 1) % foodMatrix[0].length;
                    break;
                case 3: // West
                    newX = (x - 1 + foodMatrix[0].length) % foodMatrix[0].length;
                    break;
            }

            if (!visitedFood[newY][newX]) {
                visitedFood[newY][newX] = true;
                distance += distanceMatrix[y][x];
                if (foodMatrix[newY][newX] == 1) {
                    foodCollected++;
                    distance += 10; // Adăugăm o penalizare suplimentară pentru hrana colectată
                }
            }

            x = newX;
            y = newY;
        }

        // Adăugăm o penalizare pentru traseul total, astfel încât să favorizăm traseele mai scurte
        distance -= 5 * path.length;

        // Adăugăm un bonus pentru numărul total de hrăniri colectate
        fitness = distance + 20 * foodCollected;
    }

    int[] getAllowedDirections(int prevDirection) {
        switch (prevDirection) {
            case 0: // North
                return new int[]{2, 3};
            case 1: // South
                return new int[]{2, 3};
            case 2: // East
                return new int[]{0, 1};
            case 3: // West
                return new int[]{0, 1};
            default:
                return new int[]{};
        }
    }
}

