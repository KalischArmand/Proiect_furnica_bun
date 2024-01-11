package org.example;

public class Main {
    public static void main(String[] args) {
        AntColony antColony = new AntColony(10, 5);
        antColony.localSearchEvolve(40, 0.01, 1, 5, 0.1);
    }
}
