import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Program of Ant Colony Optimization and Brute-Force Methods to solve TSP.
 * This class reads coordinates of houses from a file, computes the shortest path to visit all houses,
 * and visualizes the result using StdDraw library.
 *
 * @author Yuksel Ege Boyaci, Student ID: 2023400315
 * @since 03.05.2024
 */
public class YukselEgeBoyaci {

    /**
     * List to store the coordinates of houses
     */
    public static ArrayList<House> houses = new ArrayList<>();

    /**
     * List to store the best path found
     */
    public static ArrayList<House> bestPath = new ArrayList<>();

    /**
     * Variable to store the length of the best path found
     */
    public static double bestDistance = Double.MAX_VALUE;

    /**
     * The main method of the program.
     * Reads the coordinates of houses from a file, chooses the method to solve the TSP,
     * and visualizes the result.
     *
     * @param args Command line arguments (not used)
     * @throws FileNotFoundException If the input file is not found
     */
    public static void main(String[] args) throws FileNotFoundException {

        // Enable double buffering for smoother graphics
        StdDraw.enableDoubleBuffering();

        // File name containing coordinates of houses (change as needed)
        String fileName = "input05.txt";

        // Read coordinates of houses from file
        readFile(fileName);

        // Choose the method to solve TSP (1 for Brute-Force, 2 for Ant Colony Optimization)
        int chosenMethod = 2;

        if (chosenMethod == 1) {
            bruteForce(); // Solve TSP using Brute-Force method
        } else if (chosenMethod == 2) {
            antColonyOptimization(); // Solve TSP using Ant Colony Optimization method
        } else {
            System.out.println("Value for Chosen Method is invalid");
        }
    }

    /**
     * Solves the TSP using the Ant Colony Optimization method.
     */
    public static void antColonyOptimization() {
        // Various parameters for the Ant Colony Optimization algorithm
        int chosenMap = 1; // Choose between Shortest Distance Map and Pheromone Map
        long startTime = System.currentTimeMillis();
        double[][] pheromones = new double[houses.size()][houses.size()];
        // Initialize pheromone levels
        for (double[] pheromone : pheromones) {
            Arrays.fill(pheromone, 0.1);
        }
        // Define algorithm parameters
        int iterationCount = 100; // Number of iterations
        int antCountPerIteration = 50; // Number of ants per iteration
        double degradationFactor = 0.92; // Evaporation rate 
        double alpha = 1.8; // Parameter controlling influence of pheromones
        double beta = 2.5; // Parameter controlling influence of distance
        double Q = 0.0001; // Q value for updating pheromone levels
        Random rng = new Random();

        // Iterations of the Ant Colony Optimization algorithm
        for (int iteration = 0; iteration < iterationCount; iteration++) {
            for (int ant = 0; ant < antCountPerIteration; ant++) {
                // Construct path for each ant
                ArrayList<House> antPath = constructAntPath(houses, pheromones, alpha, beta, rng);
                double distance = calculatePathDistance(antPath);
                // Update best path if shorter path is found
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestPath = antPath;
                }
                // Update pheromone levels based on ant path
                updatePheromones(pheromones, antPath, Q);
            }
            // Evaporate pheromone levels
            evaporatePheromones(pheromones, degradationFactor);
        }

        // Record end time and calculate total time taken
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;

        // Reorder the best path
        reorderPath();

        // Print results to console
        System.out.println("Method: Ant Colony Optimization Method");
        consoleOutput(totalTime);

        // Draw map based on chosen map type
        if (chosenMap == 1) {
            drawShortestMap();
        } else if (chosenMap == 2) {
            drawPheromoneMap(pheromones);
        } else {
            System.out.println("Value for Chosen Map is invalid");
        }
    }


    /**
     * Helper method for the Ant Colony Optimization
     * Constructs a path for an ant using the Ant Colony Optimization method.
     *
     * @param houses     List of houses
     * @param pheromones Pheromone matrix
     * @param alpha      Parameter for controlling the influence of pheromones
     * @param beta       Parameter for controlling the influence of distance
     * @param random     Random number generator
     * @return An ArrayList representing the path of the ant
     */
    private static ArrayList<House> constructAntPath(ArrayList<House> houses, double[][] pheromones,
                                                     double alpha, double beta, Random random) {
        ArrayList<House> antPath = new ArrayList<>();
        ArrayList<House> remainingHouses = new ArrayList<>(houses);
        // Choose a random starting house for the ant
        House currentHouse = remainingHouses.remove(random.nextInt(remainingHouses.size()));
        antPath.add(currentHouse);

        // Construct the ant's path by selecting the next house based on pheromone levels and distances
        while (!remainingHouses.isEmpty()) {
            House nextHouse = selectNextHouse(currentHouse, remainingHouses, pheromones, alpha, beta, random);
            antPath.add(nextHouse);
            remainingHouses.remove(nextHouse);
            currentHouse = nextHouse;
        }

        return antPath;
    }

    /**
     * Helper method for the Ant Colony Optimization
     * Selects the next house for an ant to visit using the Ant Colony Optimization method.
     *
     * @param currentHouse    The current house
     * @param remainingHouses List of remaining houses to visit
     * @param pheromones      Pheromone matrix
     * @param alpha           Parameter for controlling the influence of pheromones
     * @param beta            Parameter for controlling the influence of distance
     * @param random          Random number generator
     * @return The next house to visit
     */
    private static House selectNextHouse(House currentHouse, ArrayList<House> remainingHouses,
                                         double[][] pheromones, double alpha, double beta, Random random) {
        double totalProbability = 0;
        double[] probabilities = new double[remainingHouses.size()];

        // Calculate probabilities for selecting each remaining house based on pheromone levels and distances
        for (int i = 0; i < remainingHouses.size(); i++) {
            House nextHouse = remainingHouses.get(i);
            double pheromone = Math.pow(pheromones[houses.indexOf(currentHouse)][houses.indexOf(nextHouse)], alpha);
            double distance = 1 / Math.pow(distance(currentHouse, nextHouse), beta);
            double probability = pheromone * distance;
            probabilities[i] = probability;
            totalProbability += probability;
        }

        // Choose the next house based on the calculated probabilities
        double randomProbability = random.nextDouble(0, totalProbability);
        double probability = 0;
        for (int i = 0; i < probabilities.length; i++) {
            probability += probabilities[i];
            if (randomProbability <= probability) {
                return remainingHouses.get(i);
            }
        }
        // If no house is selected based on probabilities, return the last remaining house
        return remainingHouses.getLast();
    }

    /**
     * Helper method for the Ant Colony Optimization
     * Updates the pheromone levels on the edges of the graph based on the given path.
     *
     * @param pheromones Pheromone matrix
     * @param path       The path taken by an ant
     * @param Q          Q value
     */
    private static void updatePheromones(double[][] pheromones, ArrayList<House> path, double Q) {
        double pheromoneDelta = Q / calculatePathDistance(path);
        // Update pheromone levels on edges of the graph based on the given path
        for (int i = 0; i < path.size() - 1; i++) {
            int fromIndex = houses.indexOf(path.get(i));
            int toIndex = houses.indexOf(path.get(i + 1));
            pheromones[fromIndex][toIndex] += pheromoneDelta;
            pheromones[toIndex][fromIndex] = pheromones[fromIndex][toIndex];
        }
    }

    /**
     * Helper method for the Ant Colony Optimization
     * Evaporates the pheromone levels on all edges of the graph.
     *
     * @param pheromones        Pheromone matrix
     * @param degradationFactor Factor by which the pheromones evaporate
     */
    private static void evaporatePheromones(double[][] pheromones, double degradationFactor) {
        // Evaporate pheromone levels on all edges of the graph by the given degradation factor
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[i].length; j++) {
                pheromones[i][j] *= degradationFactor;
            }
        }
    }



    /**
     * Solves the TSP using the brute-force method.
     */
    public static void bruteForce() {
        // Record start time
        long startTime = System.currentTimeMillis();

        // Convert list of houses to array for permutation
        House[] houseArray = houses.toArray(new House[houses.size()]);
        // Generate all permutations of houses and calculate distances for each permutation
        permute(houseArray, 2);

        // Record end time and calculate total time taken
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;

        // Reorder the best path
        reorderPath();

        // Print results to console
        System.out.println("Method: Brute-Force Method");
        consoleOutput(totalTime);

        // Draw map showing shortest path
        drawShortestMap();
    }

    /**
     * Helper method for the Brute-Force Method
     * Generates all permutations of the given array and calculates the distance for each permutation.
     * Updates the best path if a shorter path is found.
     *
     * @param houseArray The array to generate permutations for
     * @param k          The current index
     */
    private static void permute(House[] houseArray, int k) {
        if (k == houseArray.length) {
            // Calculate distance for the current permutation
            double distance = calculatePathDistance(new ArrayList<>(Arrays.asList(houseArray)));
            // Update best path if shorter path is found
            if (distance < bestDistance) {
                bestDistance = distance;
                bestPath.clear();
                bestPath.addAll(Arrays.asList(houseArray));
            }
        } else {
            for (int i = k; i < houseArray.length; i++) {
                // Swap elements at indices k and i
                House temp = houseArray[i];
                houseArray[i] = houseArray[k];
                houseArray[k] = temp;
                // Recursively generate permutations for the remaining elements
                permute(houseArray, k + 1);
                // Restore original order for backtracking
                temp = houseArray[k];
                houseArray[k] = houseArray[i];
                houseArray[i] = temp;
            }
        }
    }

    /**
     * Reads coordinates of houses from a file and creates the 'houses' arrayList.
     *
     * @param fileName The name of the file to read from
     * @throws FileNotFoundException If the specified file is not found
     */
    private static void readFile(String fileName) throws FileNotFoundException {
        try {
            File file = new File(fileName);
            Scanner inputFile = new Scanner(file);
            int houseNumber = 1;
            // Read coordinates from each line and create House objects
            while (inputFile.hasNextLine()) {
                String[] line = inputFile.nextLine().split(",");
                double x = Double.parseDouble(line[0]);
                double y = Double.parseDouble(line[1]);
                House house = new House(x, y, houseNumber);
                houses.add(house);
                houseNumber++;
            }
            inputFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }

    /**
     * Calculates the distance between two houses.
     *
     * @param house1 The first house
     * @param house2 The second house
     * @return The distance between the two houses
     */
    private static double distance(House house1, House house2) {
        return Math.sqrt(Math.pow(house1.x - house2.x, 2) + Math.pow(house1.y - house2.y, 2));
    }


    /**
     * Calculates the total distance of a given path by summing up the distances between consecutive houses.
     *
     * @param path The path to calculate the distance for
     * @return The total distance of the path
     */
    private static double calculatePathDistance(ArrayList<House> path) {
        double totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            // Calculate distance between consecutive houses and accumulate total distance
            totalDistance += distance(path.get(i), path.get(i + 1));
        }
        // Add distance from last house back to the starting house
        totalDistance += distance(path.getLast(), path.getFirst());
        return totalDistance;
    }

    /**
     * Prints the results to the console.
     *
     * @param totalTime The total time taken to find the shortest path
     */
    private static void consoleOutput(double totalTime) {
        // Print shortest distance and path to console
        System.out.println("Shortest Distance: " + String.format("%.5f", bestDistance));
        System.out.print("Shortest Path: [");
        for (int i = 0; i < bestPath.size(); i++) {
            // Print house numbers in the shortest path
            House currentHouse = bestPath.get(i);
            int houseNumber = houses.indexOf(currentHouse) + 1;
            System.out.print(houseNumber);
            if (i != bestPath.size() - 1) {
                System.out.print(", ");
            }
        }
        // Print total time taken to find shortest path
        System.out.println(", 1]");
        System.out.println("Time it takes to find the shortest path: " +
                String.format("%.2f", totalTime) + " seconds.");
    }

    /**
     * Reorders the best path to start from the first house.
     */
    private static void reorderPath() {
        // Find index of the starting house in the best path
        int startIndex = bestPath.indexOf(houses.getFirst());
        // Reorder best path to start from the first house
        ArrayList<House> reorderedPath = new ArrayList<>();
        reorderedPath.addAll(bestPath.subList(startIndex, bestPath.size()));
        reorderedPath.addAll(bestPath.subList(0, startIndex));
        bestPath = reorderedPath;
    }


    /**
     * Draws the map with the shortest path found using StdDraw library.
     */
    private static void drawShortestMap() {
        // Set up canvas for drawing
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);

        // Draw shortest path
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.005);
        for (int i = 0; i < bestPath.size() - 1; i++) {
            // Draw line between consecutive houses
            StdDraw.line(bestPath.get(i).x, bestPath.get(i).y, bestPath.get(i + 1).x, bestPath.get(i + 1).y);
        }
        // Draw line connecting last house to first house to complete the path
        StdDraw.line(bestPath.getFirst().x, bestPath.getFirst().y, bestPath.getLast().x, bestPath.getLast().y);

        // Draw houses
        drawHouses();
        // Show the drawing
        StdDraw.show();
    }

    /**
     * Draws the pheromone map using StdDraw library.
     *
     * @param pheromones Pheromone matrix
     */
    private static void drawPheromoneMap(double[][] pheromones) {
        // Set up canvas for drawing
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);

        // Draw lines representing pheromone trails
        for (int i = 0; i < houses.size(); i++) {
            for (int j = 0; j < houses.size(); j++) {
                StdDraw.setPenRadius(pheromones[i][j]);
                // Draw line between houses with thickness proportional to pheromone level
                StdDraw.line(houses.get(i).x, houses.get(i).y, houses.get(j).x, houses.get(j).y);
            }
        }

        // Draw houses
        drawHouses();
        // Show the drawing
        StdDraw.show();
    }

    /**
     * Draws the houses on the map using StdDraw library.
     */
    private static void drawHouses() {
        for (House house : houses) {
            // Set color for the house based on its number
            if (house.number == 1) {
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
            } else {
                StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            }
            // Draw filled circle representing the house
            StdDraw.filledCircle(house.x, house.y, 0.02);
            // Draw house number
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(house.x, house.y, Integer.toString(house.number));
        }
    }
}

/**
 * Represents a house with coordinates (x, y) and a house number.
 */
class House {

    /** The x-coordinate of the house */
    public double x;

    /** The y-coordinate of the house */
    public double y;

    /** The number of the house */
    public int number;

    /**
     * Constructs a house with the given coordinates and number.
     * @param x The x-coordinate of the house
     * @param y The y-coordinate of the house
     * @param number The number of the house
     */
    House(double x, double y, int number) {
        this.x = x;
        this.y = y;
        this.number = number;
    }
}


