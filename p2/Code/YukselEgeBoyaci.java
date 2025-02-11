import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Program for Turkey Navigation.
 * This class contains the main method to find the shortest path and visualize it on StdDraw.
 * It reads city coordinates and connections from files, prompts the user for input, calculates the shortest path,
 * and draws it on the map.
 *
 * @author Yuksel Ege Boyaci, Student ID: 2023400315
 * @since 25.03.2024
 */

public class YukselEgeBoyaci {

    /**
     * Main method to run the Turkey Navigation program.
     *
     * @param args command-line arguments (not used)
     * @throws FileNotFoundException If any required file is not found.
     */

    public static void main(String[] args) throws FileNotFoundException {

        // Define file names
        String firstFile = "city_coordinates.txt";
        String secondFile = "city_connections.txt";

        // Create instances of File class with file names.
        File fileOne = new File(firstFile);
        File fileTwo = new File(secondFile);

        // Check if files exist
        if (!fileOne.exists() || !fileTwo.exists()) {
            System.out.printf("%s or %s can not be found.", firstFile, secondFile);
            System.exit(1);
        }

        // Create instances of Scanner class with files.
        Scanner inputFileOne = new Scanner(fileOne);
        Scanner inputFileTwo = new Scanner(fileTwo);

        //Create an Arraylist for city objects.
        ArrayList<City> cities = new ArrayList<>();

        // Read city info from the first file,
        // Create city objects and store it in an Arraylist
        while (inputFileOne.hasNextLine()) {
            String line = inputFileOne.nextLine(); // Read line
            String[] lines = line.split(","); // Divide line

            // Set name and coordinates
            String cityName = lines[0].trim();
            int xCoord = Integer.parseInt(lines[1].trim());
            int yCoord = Integer.parseInt(lines[2].trim());

            // Create an instance of the City class with the name and coordinate values.
            City city = new City(cityName, xCoord, yCoord);
            cities.add(city); // Add the city object in the cities Arraylist.
        }

        inputFileOne.close(); // Close the first file

        // Read city info from the second file,
        // Get every neighbor of all cities.
        while (inputFileTwo.hasNextLine()) {
            String line = inputFileTwo.nextLine(); // Read line
            String[] lines = line.split(","); // Divide line

            // Get two city names that have connection
            String firstCity = lines[0].trim();
            String secondCity = lines[1].trim();

            // Get the city objects that have the names of connected cities
            City city1 = null;
            City city2 = null;
            for (City city : cities) {
                if (city.cityName.equals(firstCity)) {
                    city1 = city;
                } else if (city.cityName.equals(secondCity)) {
                    city2 = city;
                }
            }

            // Add connection
            if (city1 != null && city2 != null) {
                city1.connections.add(city2);
            }
        }

        inputFileTwo.close(); // Close the second file

        Scanner input = new Scanner(System.in); // Create instances of Scanner class.
        String firstInput = null;
        String secondInput = null;

        // Check if the inputs from the user actually matches a city name.
        boolean firstCheck = false;
        while (!firstCheck) {
            System.out.print("Enter starting city: ");
            firstInput = input.next(); // Get input
            firstCheck = isCityExisting(cities, firstInput); // Check if existing
            if (!firstCheck) {
                System.out.printf("City named '%s' not found. Please enter a valid city name.\n", firstInput);
            }
        }

        boolean secondCheck = false;
        while (!secondCheck) {
            System.out.print("Enter destination city: ");
            secondInput = input.next(); // Get input
            secondCheck = isCityExisting(cities, secondInput); // Check if existing
            if (!secondCheck) {
                System.out.printf("City named '%s' not found. Please enter a valid city name.\n", secondInput);
            }
        }

        // Retrieve city objects based on the city names provided by the user input.
        City startingCity = null;
        for (City city : cities) {
            if (city.cityName.equals(firstInput)) {
                startingCity = city; // Get the starting city
            }
        }
        City destinationCity = null;
        for (City city : cities) {
            if (city.cityName.equals(secondInput)) {
                destinationCity = city; // Get the destination city
            }
        }

        // Retrieve cities that are within the shortest path and store them in the shortestPath Arraylist.
        ArrayList<City> shortestPath = shortestPathFinder(startingCity, destinationCity, cities);

        StdDraw.setCanvasSize(2377 / 2, 1055 / 2); // Set canvas size
        StdDraw.setXscale(0, 2377); // Set X and Y scales.
        StdDraw.setYscale(0, 1055);

        // Place the image on canvas
        StdDraw.picture(2377.0 / 2, 1055.0 / 2, "map.png", 2377, 1055);
        StdDraw.enableDoubleBuffering(); // For faster animations

        StdDraw.setPenColor(StdDraw.GRAY); // Set the color of the pen to gray
        StdDraw.setPenRadius(0.01); // Set pen radius to 0.01
        for (City city : cities) {
            city.drawCity(); // Draw a city
        }

        StdDraw.setPenRadius(0.003); // Set pen radius to 0.003
        for (City city : cities) {
            for (City neighbor : city.connections) {
                city.drawRoad(city, neighbor); // Draw roads between cities that are connected
            }
        }

        drawShortestPath(shortestPath); // Draw the shortest path

        StdDraw.show(); // Show the canvas

    }

    /**
     * Checks if a city exists in the list of cities.
     *
     * @param cities    The list of cities to search in.
     * @param inputName The name of the city to check for existence.
     * @return true if the city exists, false otherwise.
     */

    public static boolean isCityExisting(ArrayList<City> cities, String inputName) {
        boolean check = false;
        for (City city : cities) {
            if (inputName.equals(city.cityName)) { // If input is equal to a city name
                check = true;
                return check; // return true
            }
        }
        return check; // else return false
    }

    /**
     * Finds the index of a city in the arraylist of cities.
     *
     * @param cities      The list of cities to search in.
     * @param unknownCity The city whose index is to be found.
     * @return The index of the city in the list.
     */

    public static int indexFinder(ArrayList<City> cities, City unknownCity) {
        int i = 0;
        for (City city : cities) {
            if (city.equals(unknownCity)) { // If you find the unknown city in the ArrayList
                break;
            }
            i++;
        }
        return i; // receive the index of the city
    }

    /**
     * Finds the shortest path between two cities on the map.
     *
     * @param startingCity    The starting city.
     * @param destinationCity The destination city.
     * @param cities          The arraylist of cities on the map.
     * @return The shortest path between the starting and destination cities.
     */

    public static ArrayList<City> shortestPathFinder(City startingCity, City destinationCity, ArrayList<City> cities) {
        // Initialize variables
        int numberOfLocations = cities.size(); // Set the number of cities from the length of the cities ArrayList
        double[][] map = new double[numberOfLocations][numberOfLocations]; // Set 2D array for the map
        double maxVal = Double.MAX_VALUE; // Set the maximum value

        // Initialize arrays for shortest distances, visited cities, and shortest path cities
        double[] shortestDistances = new double[numberOfLocations];
        Arrays.fill(shortestDistances, maxVal); // Fill the array with the maximum value
        shortestDistances[indexFinder(cities, startingCity)] = 0; // Set the distance of the starting city to zero

        int cityIndex;
        int neighborCityIndex = -1;

        // Construct the map with distances between cities
        for (City city : cities) {
            cityIndex = indexFinder(cities, city);
            City neighborCity = null;
            for (City neighbor : city.connections) {
                for (int j = 0; j < cities.size(); j++) {
                    if (cities.get(j).equals(neighbor)) {
                        neighborCity = cities.get(j);
                        neighborCityIndex = j;
                        break;
                    }
                }
                if (neighborCity != null) {
                    double distance = city.distanceCalculator(city, neighborCity);
                    map[cityIndex][neighborCityIndex] = distance;
                    map[neighborCityIndex][cityIndex] = distance;
                }
            }
        }

        // Initialize arrays for visited cities and shortest path cities
        boolean[] visited = new boolean[numberOfLocations]; // Set an array to check if a city is visited
        int[] shortestPathCities = new int[numberOfLocations];
        Arrays.fill(shortestPathCities, -1); // Fill the array with -1

        // Find the shortest path using Dijkstra's algorithm
        for (int count = 0; count < numberOfLocations - 1; count++) {
            double min = 999999999.9;
            int minIndex = -1;

            // Find the next closest unvisited city
            for (int location = 0; location < shortestDistances.length; location++) {
                if (!visited[location] && shortestDistances[location] <= min) {
                    min = shortestDistances[location];
                    minIndex = location;
                }
            }
            visited[minIndex] = true;

            // If the destination city is reached, stop the algorithm
            if (minIndex == indexFinder(cities, destinationCity)) {
                break;
            }

            // Update shortest distance and shortest path cities
            for (int neighborIndex = 0; neighborIndex < numberOfLocations; neighborIndex++) {
                if (shortestDistances[minIndex] + map[minIndex][neighborIndex] < shortestDistances[neighborIndex]
                        && !visited[neighborIndex] && map[minIndex][neighborIndex] != 0
                        && shortestDistances[minIndex] != maxVal) {

                    shortestDistances[neighborIndex] = shortestDistances[minIndex] + map[minIndex][neighborIndex];
                    shortestPathCities[neighborIndex] = minIndex;
                }
            }
        }

        // Reconstruct the shortest path
        ArrayList<City> shortestPath = new ArrayList<>();

        // If there is no optimal path, specify that there's no path
        if (shortestDistances[indexFinder(cities, destinationCity)] == Double.MAX_VALUE) {
            System.out.println("No path could be found.");
            return shortestPath;
        }

        // Starting from the destination city, construct the shortest path backwards
        int shortestPathCity = indexFinder(cities, destinationCity);
        while (shortestPathCity != -1) {
            shortestPath.add(cities.get(shortestPathCity));
            shortestPathCity = shortestPathCities[shortestPathCity];
        }

        // Reverse the shortest path
        int start = 0;
        int end = shortestPath.size() - 1;
        while (start < end) {
            City city = shortestPath.get(start);
            shortestPath.set(start, shortestPath.get(end));
            shortestPath.set(end, city);
            start++;
            end--;
        }

        // Print the total distance and path
        System.out.printf("Total Distance: %.2f. Path: ", shortestDistances[indexFinder(cities, destinationCity)]);
        int i1 = 0;
        int i2 = shortestPath.size() - 1;
        while (i1 < i2) {
            City city = shortestPath.get(i1);
            System.out.printf("%s -> ", city.cityName);
            i1++;
        }
        System.out.printf("%s", shortestPath.getLast().cityName);

        return shortestPath;
    }

    /**
     * Draws the shortest path between cities on the map.
     *
     * @param shortestPath The shortest path between cities.
     */
    public static void drawShortestPath(ArrayList<City> shortestPath) {
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE); // Set pen color to blue
        StdDraw.setPenRadius(0.01); // Set pen radius to 0.01
        for (City city : shortestPath) {
            city.drawCity(); // Draw city
        }
        int i1 = 0;
        int i2 = shortestPath.size() - 1;

        // Draw the shortest path
        while (i1 < i2) {
            City city = shortestPath.get(i1);
            City neighborCity = shortestPath.get(i1 + 1);
            city.drawRoad(city, neighborCity);
            i1++;
        }
    }
}




